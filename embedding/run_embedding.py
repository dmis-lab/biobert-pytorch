# coding=utf-8
# Copyright 2018 The Google AI Language Team Authors and The HuggingFace Inc. team.
# Copyright (c) 2018, NVIDIA CORPORATION.  All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
""" Returning embedding of input text """


import logging
import os
import sys
from dataclasses import dataclass, field
from typing import Dict, List, Optional, Tuple
from torch.utils.data.dataloader import DataLoader

import numpy as np
import torch
from torch import nn
import h5py
import pdb
from tqdm import tqdm
from transformers import (
    AutoConfig,
    AutoTokenizer,
    AutoModel,
    HfArgumentParser,
    set_seed,
)
from utils_embedding import EmbeddingDataset, data_collator

logger = logging.getLogger(__name__)
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

@dataclass
class ModelArguments:
    """
    Arguments pertaining to which model/config/tokenizer we are going to fine-tune from.
    """

    model_name_or_path: str = field(
        metadata={"help": "Path to pretrained model or model identifier from huggingface.co/models"}
    )
    config_name: Optional[str] = field(
        default=None, metadata={"help": "Pretrained config name or path if not the same as model_name"}
    )
    tokenizer_name: Optional[str] = field(
        default=None, metadata={"help": "Pretrained tokenizer name or path if not the same as model_name"}
    )
    use_fast: bool = field(default=False, metadata={"help": "Set this flag to use fast tokenization."})
    # If you want to tweak more attributes on your tokenizer, you should do it in a distinct script,
    # or just modify its tokenizer_config.json.
    cache_dir: Optional[str] = field(
        default=None, metadata={"help": "Where do you want to store the pretrained models downloaded from s3"}
    )

@dataclass
class DataArguments:
    """
    Arguments pertaining to what data we are going to input our model for training and eval.
    """
    
    data_path: str = field(
        metadata={"help": "The input data path in .txt format."}
    )
    output_path: str = field(
        metadata={"help": "The file name of embedding output (.h5)"}
    )

@dataclass
class EmbeddingArguments:
    """
    Arguments pertaining to what data we are going to input our model for training and eval.
    """

    pooling: str = field(
        default='none',
        metadata={"help": "Pooling method: none, first, mean, sum (default:none)"}
    )
    batch_size: int = field(
        default=32,
        metadata={"help": "Batch size to embed in batch"}
    )
    max_seq_length: int = field(
        default=128,
        metadata={
            "help": "The maximum total input sequence length after tokenization. Sequences longer "
            "than this will be truncated, sequences shorter will be padded."
        },
    )

class Embedder:
    """
    Embedder is a simple but feature-complete training and eval loop for PyTorch,
    optimized for Transformers.
    """

    model: AutoModel
    args: EmbeddingArguments
    embed_dataset: EmbeddingDataset
    output_path: str

    def __init__(
        self,
        model: AutoModel,
        args: EmbeddingArguments,
        embed_dataset: EmbeddingDataset=None,
        output_path: str=""
    ):
        """
        Embedder is a simple but feature-complete training and eval loop for PyTorch,
        optimized for Transformers.
        """
        self.model = model.to(device)
        self.args = args
        self.embed_dataset = embed_dataset
        self.output_path = output_path

    def get_embed_dataloader(self) -> DataLoader:
        if self.embed_dataset is None:
            raise ValueError("Embedder: embedding requires a embed_dataset.")

        data_loader = DataLoader(
            self.embed_dataset,
            batch_size=self.args.batch_size,
            collate_fn=data_collator
        )

        return data_loader
    
    def num_examples(self, dataloader: DataLoader) -> int:
        """
        Helper to get num of examples from a DataLoader, by accessing its Dataset.
        """
        return len(dataloader.dataset)
    
    def embed(self) -> Tuple:
        """
        Run prediction and return predictions and potential metrics.

        Depending on the dataset and your use case, your test dataset may contain labels.
        In that case, this method will also return metrics, like in evaluate().
        """
        embed_dataloader = self.get_embed_dataloader()

        return self._embedding_loop(embed_dataloader)

    def _embedding_loop(
        self, dataloader: DataLoader
    ) -> Tuple:
        """
        Prediction/evaluation loop, shared by `evaluate()` and `predict()`.

        Works both with or without labels.
        """

        model = self.model
        batch_size = dataloader.batch_size
        logger.info("***** Running embedding *****")
        logger.info("  Num examples = %d", self.num_examples(dataloader))
        logger.info("  Batch size = %d", batch_size)
        model.eval()

        # prepare for hdf5 file stream
        f = h5py.File(self.output_path, 'w')
        
        for inputs in tqdm(dataloader, desc='embedding'):
            for k, v in inputs.items():
                if isinstance(v, torch.Tensor):
                    inputs[k] = v.to(device)

            metadata = inputs['metadata']
            del inputs['metadata']
            
            with torch.no_grad():
                outputs = model(**inputs)
            last_hidden_states = outputs[0].detach()

            # batch process (fast)
            embeddings = []
            if self.args.pooling == 'first':
                embeddings = last_hidden_states[:,0,:]
            elif self.args.pooling == 'sum' or self.args.pooling == 'mean':
                # masking [CLS] and [SEP]
                attention_mask = inputs['attention_mask'].detach()
                attention_mask = torch.nn.functional.pad(attention_mask[:,2:],(1,1)) # 2 means [CLS] and [SEP]

                # extract the hidden state where there's no masking
                attention_mask = attention_mask.unsqueeze(-1).expand(last_hidden_states.shape)
                sub_embeddings = (attention_mask.to(torch.float)*last_hidden_states)

                # summation
                embeddings = sub_embeddings.sum(dim=1)
                
                # mean
                if self.args.pooling == 'mean':
                    attention_mask = attention_mask[:,:,0].sum(dim=-1).unsqueeze(1)
                    embeddings = embeddings/attention_mask.to(torch.float)
            
            elif self.args.pooling == 'none':
                for embed, attention_mask in zip(last_hidden_states, inputs['attention_mask']):
                    token_embed = embed[0:attention_mask.sum()]
                    embeddings.append(token_embed)
                    
            # save into hdf5 file
            for embedding, each_metadata in zip(embeddings,metadata):
                text_id=u"{}".format(each_metadata['text'])
                dg = f.get(text_id) or f.create_group(text_id)        
                if not dg.get('embedding'):
                    dg.create_dataset('embedding', data=embedding.cpu()) 
        
        # close hdf5 file stream
        f.close()

def main():
    # We now keep distinct sets of args, for a cleaner separation of concerns.
    parser = HfArgumentParser((ModelArguments, DataArguments, EmbeddingArguments))
    model_args, data_args, embed_args = parser.parse_args_into_dataclasses()

    # Setup logging
    logging.basicConfig(
        format="%(asctime)s - %(levelname)s - %(name)s -   %(message)s",
        datefmt="%m/%d/%Y %H:%M:%S",
        level=logging.INFO,
    )

    # argument check
    if embed_args.pooling not in ['none', 'first', 'mean', 'sum']:
        logging.warn('pooling should be none, first, mean, or sum')
        return

    # Load pretrained model and tokenizer
    config = AutoConfig.from_pretrained(
        model_args.config_name if model_args.config_name else model_args.model_name_or_path,
    )
    tokenizer = AutoTokenizer.from_pretrained(
        model_args.tokenizer_name if model_args.tokenizer_name else model_args.model_name_or_path,
        use_fast=model_args.use_fast,
    )
    model = AutoModel.from_pretrained(
        model_args.model_name_or_path,
        from_tf=bool(".ckpt" in model_args.model_name_or_path),
        config=config,
        cache_dir=model_args.cache_dir,
    )
    
    # Get datasets
    embed_dataset = EmbeddingDataset(
        data_path=data_args.data_path,
        tokenizer=tokenizer,
        max_seq_length=embed_args.max_seq_length,
    )
    
    # Initialize our Embedder
    embedder = Embedder(
        model=model,
        args=embed_args,
        embed_dataset=embed_dataset,
        output_path=data_args.output_path
    )
    
    # run embed and save it to hdf5
    embedder.embed()
    print("done")
    
def _mp_fn(index):
    # For xla_spawn (TPUs)
    main()


if __name__ == "__main__":
    main()

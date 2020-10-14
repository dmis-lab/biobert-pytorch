import logging
from dataclasses import dataclass
from typing import List, Dict, Optional
from transformers import PreTrainedTokenizer, is_torch_available
import pdb
from tqdm import tqdm
logger = logging.getLogger(__name__)

@dataclass
class InputFeatures:
    """
    A single set of features of data.
    Property names are the same names as the corresponding inputs to a model.
    """

    input_ids: List[int]
    attention_mask: List[int]
    token_type_ids: Optional[List[int]] = None
    metadata: Optional[dict] = None
    
def read_texts_from_file(file_path) -> List[str]:
    with open(file_path, encoding="utf-8") as f:
        texts = f.readlines()
    
    return texts

def convert_texts_to_features(
    texts: List[str],
    max_seq_length: int,
    tokenizer: PreTrainedTokenizer,
    cls_token="[CLS]",
    sep_token="[SEP]",
) -> dict:
    """ Convert text in .txt file into input features
    """

    features = []
    for t_idx, text in tqdm(enumerate(texts), total=len(texts)):
        tokens = tokenizer.tokenize(text)

        # Account for [CLS] and [SEP] with "- 2" and with "- 3" for RoBERTa.
        special_tokens_count = tokenizer.num_special_tokens_to_add()
        if len(tokens) > max_seq_length - special_tokens_count:
            tokens = tokens[: (max_seq_length - special_tokens_count)]

        # The convention in BERT is:
        # (a) For sequence pairs:
        #  tokens:   [CLS] is this jack ##son ##ville ? [SEP] no it is not . [SEP]
        #  type_ids:   0   0  0    0    0     0       0   0   1  1  1  1   1   1
        # (b) For single sequences:
        #  tokens:   [CLS] the dog is hairy . [SEP]
        #  type_ids:   0   0   0   0  0     0   0
        #
        # Where "type_ids" are used to indicate whether this is the first
        # sequence or the second sequence. The embedding vectors for `type=0` and
        # `type=1` were learned during pre-training and are added to the wordpiece
        # embedding vector (and position vector). This is not *strictly* necessary
        # since the [SEP] token unambiguously separates the sequences, but it makes
        # it easier for the model to learn the concept of sequences.
        #
        # For classification tasks, the first vector (corresponding to [CLS]) is
        # used as as the "sentence vector". Note that this only makes sense because
        # the entire model is fine-tuned.
        tokens += [sep_token]
        segment_ids = [0] * len(tokens)

        # CLS token
        tokens = [cls_token] + tokens
        segment_ids = [0] + segment_ids

        input_ids = tokenizer.convert_tokens_to_ids(tokens)

        # The mask has 1 for real tokens and 0 for padding tokens. Only real
        # tokens are attended to.
        input_mask = [1] * len(input_ids)

        # Zero-pad up to the sequence length.
        padding_length = max_seq_length - len(input_ids)
        input_ids += [0] * padding_length
        input_mask += [0] * padding_length
        segment_ids += [0] * padding_length

        assert len(input_ids) == max_seq_length
        assert len(input_mask) == max_seq_length
        assert len(segment_ids) == max_seq_length
        
        if t_idx<5:
            logger.info("*** Example ***")
            logger.info("tokens: %s", " ".join([str(x) for x in tokens]))
            logger.info("input_ids: %s", " ".join([str(x) for x in input_ids]))
            logger.info("input_mask: %s", " ".join([str(x) for x in input_mask]))
            logger.info("segment_ids: %s", " ".join([str(x) for x in segment_ids]))

        features.append(
            InputFeatures(
                input_ids=input_ids, 
                attention_mask=input_mask, 
                token_type_ids=segment_ids,
                metadata={
                    'text':text.strip(),
                    'text_id':str(t_idx),
                    'tokens': tokens
                }
            )
        )
    return features

if is_torch_available():
    import torch
    from torch import nn
    from torch.utils.data.dataset import Dataset

    class EmbeddingDataset(Dataset):
        """
        This will be superseded by a framework-agnostic approach
        soon.
        """

        features: List[InputFeatures]
        
        def __init__(
            self,
            data_path: str,
            tokenizer: PreTrainedTokenizer,
            max_seq_length,
        ):
            logger.info(f"Creating features from dataset file at {data_path}")
            texts = read_texts_from_file(data_path)
            self.features = convert_texts_to_features(
                texts = texts,
                max_seq_length = max_seq_length,
                tokenizer = tokenizer,
            )

        def __len__(self):
            return len(self.features)

        def __getitem__(self, i) -> InputFeatures:
            return self.features[i]
        
    def data_collator(features: List[InputFeatures]) -> Dict[str, torch.Tensor]:
        first = features[0]
        batch = {}
        for k, v in first.__dict__.items():
            if k == 'metadata':
                batch[k] = [f.__dict__[k] for f in features]
            else:
                batch[k] = torch.tensor([f.__dict__[k] for f in features], dtype=torch.long)
        return batch
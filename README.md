# BioBERT-PyTorch
This repository provides the PyTorch implementation of [BioBERT](https://academic.oup.com/bioinformatics/article/36/4/1234/5566506).
You can easily use BioBERT with [transformers](https://github.com/huggingface/transformers).
This project is supported by the members of [DMIS-Lab](https://dmis.korea.ac.kr/) @ Korea University including Jinhyuk Lee, Wonjin Yoon, Minbyul Jeong, Mujeen Sung, and Gangwoo Kim.

## Installation
```bash
# Install huggingface transformers
pip install transformers==3.0.0

# Download all datasets including NER/RE/QA
./download.sh
```
Note that you should also install `torch` (see [download instruction](https://pytorch.org/)) to use `transformers`.
If the download script does not work, you can manually download the datasets [here](https://drive.google.com/file/d/1cGqvAm9IZ_86C4Mj7Zf-w9CFilYVDl8j/view?usp=sharing) which should be unzipped in the current directory (`tar -xzvf datasets.tar.gz`).

## Models
We provide following versions of BioBERT in PyTorch (click [here](https://huggingface.co/dmis-lab) to see all).
You can use BioBERT in `transformers` by setting `--model_name_or_path` as one of them (see example below).
* `dmis-lab/biobert-base-cased-v1.1`: BioBERT-Base v1.1 (+ PubMed 1M)
* `dmis-lab/biobert-large-cased-v1.1`: BioBERT-Large v1.1 (+ PubMed 1M)
* `dmis-lab/biobert-base-cased-v1.1-mnli`: BioBERT-Base v1.1 pre-trained on MNLI
* `dmis-lab/biobert-base-cased-v1.1-squad`: BioBERT-Base v1.1 pre-trained on SQuAD

For other versions of BioBERT or for Tensorflow, please see the [README](https://github.com/dmis-lab/biobert) in the original BioBERT repository.
You can convert any version of BioBERT into PyTorch with [this](https://github.com/huggingface/transformers/blob/v3.5.1/src/transformers/convert_bert_original_tf_checkpoint_to_pytorch.py).

## Example
For instance, to train BioBERT on the NER dataset (NCBI-disease), run as:

```bash
# Pre-process NER datasets
cd named-entity-recognition
./preprocess.sh

# Choose dataset and run
export DATA_DIR=../datasets/NER
export ENTITY=NCBI-disease
python run_ner.py \
    --data_dir ${DATA_DIR}/${ENTITY} \
    --labels ${DATA_DIR}/${ENTITY}/labels.txt \
    --model_name_or_path dmis-lab/biobert-base-cased-v1.1 \
    --output_dir output/${ENTITY} \
    --max_seq_length 128 \
    --num_train_epochs 3 \
    --per_device_train_batch_size 32 \
    --save_steps 1000 \
    --seed 1 \
    --do_train \
    --do_eval \
    --do_predict \
    --overwrite_output_dir
```

Please see each directory for different examples. Currently, we provide
* [embedding/](https://github.com/dmis-lab/biobert-pytorch/tree/master/embedding): BioBERT embedding.
* [named-entity-recognition/](https://github.com/dmis-lab/biobert-pytorch/tree/master/named-entity-recognition): NER using BioBERT.
* [question-answering/](https://github.com/dmis-lab/biobert-pytorch/tree/master/question-answering): QA using BioBERT.
* [relation-extraction/](https://github.com/dmis-lab/biobert-pytorch/tree/master/relation-extraction): RE using BioBERT.

Most examples are modifed from [examples](https://github.com/huggingface/transformers/tree/master/examples) in Hugging Face transformers.

## Citation
```bibtex
@article{10.1093/bioinformatics/btz682,
    author = {Lee, Jinhyuk and Yoon, Wonjin and Kim, Sungdong and Kim, Donghyeon and Kim, Sunkyu and So, Chan Ho and Kang, Jaewoo},
    title = "{BioBERT: a pre-trained biomedical language representation model for biomedical text mining}",
    journal = {Bioinformatics},
    year = {2019},
    month = {09},
    issn = {1367-4803},
    doi = {10.1093/bioinformatics/btz682},
    url = {https://doi.org/10.1093/bioinformatics/btz682},
}
```

## License and Disclaimer
Please see the LICENSE file for details. Downloading data indicates your acceptance of our disclaimer.


## Contact
For help or issues using BioBERT-PyTorch, please create an issue.

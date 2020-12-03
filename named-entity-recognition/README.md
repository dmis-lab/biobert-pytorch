# BioBERT for Named Entity Recognition

To train an NER model with BioBERT-v1.1 (base), run the command below.
Before training, please run `./preprocess.sh` to preprocess the datasets downloaded in `biobert-pytorch` (see [here](https://github.com/jhyuklee/biobert-pytorch)).

## Additional Requirements
- seqeval : Used for NER evaluation (`pip install seqeval`)

## Training
```bash
export SAVE_DIR=./output
export DATA_DIR=../datasets/NER

export MAX_LENGTH=192
export BATCH_SIZE=32
export NUM_EPOCHS=30
export SAVE_STEPS=1000
export ENTITY=NCBI-disease
export SEED=1

python run_ner.py \
    --data_dir ${DATA_DIR}/${ENTITY}/ \
    --labels ${DATA_DIR}/${ENTITY}/labels.txt \
    --model_name_or_path dmis-lab/biobert-base-cased-v1.1 \
    --output_dir ${SAVE_DIR}/${ENTITY} \
    --max_seq_length ${MAX_LENGTH} \
    --num_train_epochs ${NUM_EPOCHS} \
    --per_device_train_batch_size ${BATCH_SIZE} \
    --save_steps ${SAVE_STEPS} \
    --seed ${SEED} \
    --do_train \
    --do_eval \
    --do_predict \
    --overwrite_output_dir
```

## Evaluation Results

### BioBERT

|                |    Test Precision (%)   |    Test Recall (%)   |    Test F1 (%)   |
|----------------|:-----------------------:|:--------------------:|:----------------:|
| NCBI-disease   |          86.96          |         89.68        |       88.30      |
| BC5CDR-disease |          84.84          |         87.95        |       86.37      |
| BC5CDR-chem    |          92.66          |         93.42        |       93.04      |
| BC4CHEMD       |          91.74          |         90.57        |       91.15      |
| JNLPBA         |          70.36          |         82.58        |       75.98      |
| BC2GM          |          82.62          |         84.22        |       83.41      |
| LINNAEUS       |          92.14          |         84.36        |       88.08      |
| S800           |          69.20          |         75.88        |       72.38      |

### BERT

|                |    Test Precision (%)   |    Test Recall (%)   |    Test F1 (%)   |
|----------------|:-----------------------:|:--------------------:|:----------------:|
| NCBI-disease   |          83.44          |         87.18        |       85.27      |
| BC5CDR-disease |          82.40          |         84.17        |       83.28      |
| BC5CDR-chem    |          91.71          |         91.86        |       91.78      |
| BC4CHEMD       |          90.42          |         88.62        |       89.51      |
| JNLPBA         |          69.78          |         81.70        |       75.27      |
| BC2GM          |          80.66          |         81.20        |       80.93      |
| LINNAEUS       |          89.26          |         78.36        |       83.46      |
| S800           |          68.15          |         73.66        |       70.80      |

### RoBERTa

|                |    Test Precision (%)   |    Test Recall (%)   |    Test F1 (%)   |
|----------------|:-----------------------:|:--------------------:|:----------------:|
| NCBI-disease   |          84.65          |         85.62        |       85.13      |
| BC5CDR-disease |          79.17          |         81.89        |       80.51      |
| BC5CDR-chem    |          90.36          |         88.43        |       89.38      |
| BC4CHEMD       |          89.51          |         86.25        |       87.85      |
| JNLPBA         |          71.03          |         80.44        |       75.44      |
| BC2GM          |          80.18          |         80.25        |       80.22      |
| LINNAEUS       |          89.49          |         80.25        |       84.62      |
| S800           |          68.81          |         73.92        |       71.27      |

## Contact
For help or issues using BioBERT-PyTorch, please create an issue and tag [@minstar](https://github.com/minstar).

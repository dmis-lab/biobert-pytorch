# BioBERT for Question Answering

To train QA models with BioBERT-v1.1 (base), follow the description below.
We preprocessed the BioASQ 7b (YesNo/Factoid) dataset to the SQuAD format as decribed in [Jeong et al, 2020](https://arxiv.org/abs/2007.00217).

## Models
Besides `dmis-lab/biobert-base-cased-v1.1` and `dmis-lab/biobert-large-cased-v1.1`, we additionally provide two BioBERT models pre-trained on MNLI and SQuAD, respectively:
- `dmis-lab/biobert-base-cased-v1.1-mnli`: BioBERT pre-trained on MNLI
- `dmis-lab/biobert-base-cased-v1.1-squad`: BioBERT pre-trained on SQuAD

## Additional Requirements
- pandas : (Factoid) Transforms the SQuAD prediction file into the BioASQ format (`pip install pandas`)
- tensorboardX : (Factoid) SummaryWriter module (`pip install tensorboardX`)

## Yes/No QA

```bash
export SAVE_DIR=./output
export DATA_DIR=../datasets/QA/BioASQ
export OFFICIAL_DIR=./scripts/bioasq_eval

export BATCH_SIZE=12
export LEARNING_RATE=8e-6
export NUM_EPOCHS=3
export MAX_LENGTH=384
export SEED=0

# Train
python run_yesno.py \
    --model_type bert \
    --model_name_or_path dmis-lab/biobert-base-cased-v1.1 \
    --do_train \
    --train_file ${DATA_DIR}/BioASQ-train-yesno-7b.json \
    --per_gpu_train_batch_size ${BATCH_SIZE} \
    --learning_rate ${LEARNING_RATE} \
    --num_train_epochs ${NUM_EPOCHS} \
    --max_seq_length ${MAX_LENGTH} \
    --seed ${SEED} \
    --output_dir ${SAVE_DIR}

# Evaluation
python run_yesno.py \
    --model_type bert \
    --model_name_or_path ${SAVE_DIR} \
    --do_eval \
    --predict_file ${DATA_DIR}/BioASQ-test-yesno-7b.json \
    --golden_file ${DATA_DIR}/7B_golden.json \
    --per_gpu_train_batch_size ${BATCH_SIZE} \
    --official_eval_dir ${OFFICIAL_DIR} \
    --output_dir ${SAVE_DIR}
```
Training with the previously defined hyper-parameters yields the following results:
```bash
Accuracy = 69.29
macro F1 = 65.75
F1 - yes = 76.76
F1 - no  = 54.74
```

## Factoid QA

```bash
export SAVE_DIR=./output
export DATA_DIR=../datasets/QA/BioASQ
export OFFICIAL_DIR=./scripts/bioasq_eval

export BATCH_SIZE=12
export LEARNING_RATE=8e-6
export NUM_EPOCHS=3
export MAX_LENGTH=384
export SEED=0

# Train
python run_factoid.py \
    --model_type bert \
    --model_name_or_path dmis-lab/biobert-base-cased-v1.1 \
    --do_train \
    --train_file ${DATA_DIR}/BioASQ-train-factoid-7b.json \
    --per_gpu_train_batch_size ${BATCH_SIZE} \
    --learning_rate ${LEARNING_RATE} \
    --num_train_epochs ${NUM_EPOCHS} \
    --max_seq_length ${MAX_LENGTH} \
    --seed ${SEED} \
    --output_dir ${SAVE_DIR}

# Evaluation
python run_factoid.py \
    --model_type bert \
    --model_name_or_path ${SAVE_DIR} \
    --do_eval \
    --predict_file ${DATA_DIR}/BioASQ-test-factoid-7b.json \
    --golden_file ${DATA_DIR}/7B_golden.json \
    --per_gpu_eval_batch_size ${BATCH_SIZE} \
    --max_seq_length ${MAX_LENGTH} \
    --seed ${SEED} \
    --official_eval_dir ${OFFICIAL_DIR} \
    --output_dir ${SAVE_DIR}
```
Training with the previously defined hyper-parameters yields the following results:
```bash
S. Accuracy = 33.33
L. Accuracy = 45.68
MRR         = 38.53
```

## Evaluation Results
### YesNo

|              | Acc.  | F1    | F1-yes | F1-no |
|--------------|-------|-------|--------|-------|
| BERT         | 64.29 | 58.04 | 74.23  | 41.86 |
| roBERTa      | 65.00 | 60.55 | 73.80  | 47.31 |
| BioBERT      | 69.29 | 65.75 | 76.76  | 54.74 |
| BioBERT-MNLI | 90.71 | 89.02 | 93.33  | 84.71 |

### Factoid

|               | S.Acc | L.Acc | MRR   |
|---------------|-------|-------|-------|
| BERT          | 24.69 | 39.51 | 30.91 |
| roBERTa       | 27.78 | 39.51 | 31.56 |
| BioBERT       | 31.48 | 45.68 | 37.13 |
| BioBERT-SQuAD | 40.12 | 59.26 | 48.34 |

## Contact
For help or issues using BioBERT-PyTorch, please create an issue and tag [@gangwook](https://github.com/gangwook).

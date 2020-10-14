# BioBERT for Relation Extraction

To train an RE model with BioBERT-v1.1 (base), please use following command line:
Before training, please run `./preprocess.sh` to preprocess the datasets downloaded in `biobert-pytorch` (see [here](https://github.com/jhyuklee/biobert-pytorch)).

## Additional Requirements
- sklearn: Used for RE evaluation (`pip install scikit-learn`)
- pandas : Used for RE evaluation (`pip install pandas`)

## Training
```bash
export SAVE_DIR=./output
export DATA="GAD"
export SPLIT="1"
export DATA_DIR=../datasets/RE/${DATA}/${SPLIT}
export ENTITY=${DATA}-${SPLIT}

export MAX_LENGTH=128
export BATCH_SIZE=32
export NUM_EPOCHS=3
export SAVE_STEPS=1000
export SEED=1

python run_re.py \
    --task_name SST-2 \
    --config_name bert-base-cased \
    --data_dir ${DATA_DIR} \
    --model_name_or_path dmis-lab/biobert-base-cased-v1.1 \
    --max_seq_length ${MAX_LENGTH} \
    --num_train_epochs ${NUM_EPOCHS} \
    --per_device_train_batch_size ${BATCH_SIZE} \
    --save_steps ${SAVE_STEPS} \
    --seed ${SEED} \
    --do_train \
    --do_predict \
    --learning_rate 5e-5 \
    --output_dir ${SAVE_DIR}/${ENTITY} \
    --overwrite_output_dir
```

## Evaluation
```bash
python ./scripts/re_eval.py --output_path=${SAVE_DIR}/${ENTITY}/test_results.txt --answer_path=${DATA_DIR}/test_original.tsv
```
To evaluate the prediction, please use `scripts/re_eval.py` file. 
For an example running script for 10-cv experiment, please task a look at `run_re_10cv.sh` and `scripts/re_eval_10cv.sh`.

## Evaluation Results
### BioBERT

|                |     Precision (%)    |     Recall (%)    |     F1 (%)    |
|----------------|:--------------------:|:-----------------:|:-------------:|
| GAD            |         77.09        |        88.50      |     82.37     |
| EUADR          |         77.48        |        96.15      |     85.13     |

## Contact
For help or issues using BioBERT-PyTorch, please create an issue and tag [@wonjininfo](https://github.com/wonjininfo).

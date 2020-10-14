#!/bin/bash
ENTITIES="euadr GAD"
MAX_LENGTH=128

for ENTITY in $ENTITIES
do
	echo "***** " $ENTITY " Preprocessing Start *****"
	for SPLIT in {1..10}
	do
		DATA_DIR=../datasets/RE/$ENTITY/$SPLIT
		
		mv $DATA_DIR/train.tsv $DATA_DIR/train_original.tsv
		mv $DATA_DIR/test.tsv $DATA_DIR/test_original.tsv
		
		# Preprocess for BERT-based models
		python scripts/preprocess.py $DATA_DIR/train_original.tsv train  > $DATA_DIR/train.tsv
		python scripts/preprocess.py $DATA_DIR/test_original.tsv test > $DATA_DIR/test.tsv
		
	done
	echo "***** " $ENTITY " Preprocessing Done *****"
done

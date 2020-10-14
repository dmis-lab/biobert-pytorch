DATA="euadr"

for SPLIT in {1..10}
do
  ENTITY=$DATA-$SPLIT

  echo "***** " $DATA " test score " $SPLIT " *****"
  python scripts/re_eval.py \
    --output_path=./output/$ENTITY/test_results.txt \
    --answer_path=../datasets/RE/$DATA/$SPLIT/test_original.tsv
done

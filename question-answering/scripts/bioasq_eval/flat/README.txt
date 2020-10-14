This directory contains EvalMeasuresBioASQ Java project. 

This project includes evaluation Java Source Package for BioASQ flat evaluation measures. 
converters Source Package also included, for conversion of files in integer format needed for both flat and hierarchical measures. 

Libraries used by EvalMeasuresBioASQ are located in \BioASQEvaluation\dist\lib

Instructions for BioASQ evaluation measures
-----------------------------------------------

Task A
---------

1. Before running the measures the results of the system and the golden standard results need to be mapped to the integer-based format:

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar converters.MapMeshResults mesh/mapping.txt system_A_results.txt system_A_results_mapped.txt

where file system_A_results.txt contains a line (with labels seperated by space) for each test instance e.g.:
D05632 D04322
D033321 D98766 D98765
...

A file named system_A_results_mapped.txt will be created containing the coressponding integer labels e.g.:
45 67
23 90 89
...

The same procedure is repeated for the file with the true labels.

2. For running the flat measures the following command is invoked:

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.Evaluator true_labels_mapped.txt system_A_results_mapped.txt

The program will print to the standard output the following numbers: accuracy EbP EbR EbF MaP MaR MaF MiP MiR MiF


3. For running the hierarchical measures:
./hierarchical/bin/HEMKit ./mesh/mesh_hier_int.txt true_labels_mapped.txt system_A_results_mapped.txt 4 5

will result to the following output: hP hR hF LCA-P LCA-R LCA-F



Task B
---------

1. For running the measures for Task B, phase A the following command is invoked:

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.EvaluatorTask1b -phaseA -e 5 golden_file.json system_response.json

will result to the following output: MPrec concepts, MRec concepts, MF1 concepts, MAP concepts, GMAP concepts, MPrec documents, MRec documents, MF1 documents, MAP documents, GMAP documents, MPrec snippets, MRec snippets, MF1 snippets, MAP snippets, GMAP snippets, MPrec triples, MRec triples, MF1 triples, MAP triples, GMAP triples. 

or

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.EvaluatorTask1b -phaseA -e 5 golden_file.json system_response.json -verbose

2. For running the measures for Task B, phase B the following command is invoked:

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.EvaluatorTask1b -phaseB -e 5 golden_file.json system_response.json

will result to the following output: YesNo Acc, Factoid Strict Acc, Factoid Lenient Acc, Factoid MRR, List Prec, List Rec, List F1, YesNo macroF1, YesNo F1 yes, YesNo F1 no.

or 

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.EvaluatorTask1b -phaseB -e 5 golden_file.json system_response.json -verbose

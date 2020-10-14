
Evaluation Measures for BioASQ Challenge
-----------------------------------------------

Instructions for BioASQ evaluation measures
-----------------------------------------------

Task A
---------

The package contains two folders "flat/" and "hierarchical/" corresponding to the flat and hierarchical measures used during the evaluation of the challenge Task a. 
Additionally, a folder "mesh/" contains the MESH 2016 hierarchy in parent-child relations both in the original DescriptorID format (mesh_hierarchy.txt) and in mapped format using integers (mesh_hiearchy_int.txt) as well as the corresponding mapping (mapping.txt). 

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

2. For running the measures for Task B, phase B the following command is invoked:

java -Xmx10G -cp $CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar evaluation.EvaluatorTask1b -phaseB -e 5 golden_file.json system_response.json


Task C
---------
Folder taskc contains evaluation measures for Task C on Funding Information Extraction From Biomedical Literature

For running the measures for task C, i.e. micro-recall, the following commmand is invoked in python :

python eval_script_5c.py -x /full/path/to/groundtruth/test.json -y /full/path/to/user/results.json -z /full/path/to/TaskCAgenciesParentChild.txt

The flags passed correspond to the following:
-x: absolute path to ground truth .json file
-y: absolute path to user's predictions .json file
-z: absolute path to the hierarchy .txt file. If this is ommited, hierarchy will not be taken into account.

The output printed on stdout will be the Grant ID Micro Recall, Grant Agency Micro Recall and Full Grant Micro Recall achieved by the system result file, according to the [evaluation section in the guidelines](http://participants-area.bioasq.org/general_information/Task5c/).


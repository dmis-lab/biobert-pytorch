# Evaluation Script for BioASQ Task C Challenge

##  BioASQ Task C on Funding Information Extraction From Biomedical Literature
In this task, the participants are asked to extract grant ids and grant agencies from the full text of PumMed documents, available in PubMed Central. Annotations from PubMed are used to evaluate the information extraction performance of participating systems


## Instructions
The evaluation script contains the functions needed to evaluate the results of the systems, alongside some utility functions for hierarchy extraction and equality comparison between agencies. All functions can be imported and used as standalone evaluation functions. By default, all evaluation measures needed by the challenge are calculated, also taking into account hierarchy. 

There are **no requirements** to be pre-installed.
The only prerequisite is the [hierarchy of the selected Agencies as provided by the organizers](participants-area.bioasq.org/taskCAgencyParentChild/).
The file of the results to be evaluated (i.e. results.json) should follow all the submission restrictions described in the Guidelines of task c, under section "Required Answers".

## Example usage:
In **python2**:

```python
python eval_script_5c.py -x /full/path/to/groundtruth/test.json -y /full/path/to/user/results.json -z /full/path/to/TaskCAgenciesParentChild.txt
```
The flags passed correspond to the following:
- -x: absolute path to ground truth .json file
- -y: absolute path to user's predictions .json file
- -z: absolute path to the hierarchy .txt file. If this is ommited, hierarchy will not be taken into account.

The output printed on stdout will be the **Grant ID Micro Recall**, **Grant Agency Micro Recall** and **Full Grant Micro Recall** achieved by the system result file, according to the [evaluation section in the guidelines](http://participants-area.bioasq.org/general_information/Task5c/).

## Questions/Errors
If you have any questions or stumble upon an error please contact:
Bougiatiotis Konstantinos, NCSR ‘DEMOKRITOS’, E-mail: bogas.ko@gmail.com


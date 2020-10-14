This directory contains the software of HEMKit, a collection of hierarchical 
evaluation measures. The software was developed for the journal paper: 

Aris Kosmopoulos, Ioannnis Partalas, Eric Gaussier, George Paliouras 
and Ion Androutsopoulos, "Evaluation Measures for Hierarchical Classification: 
a unified view through two generic frameworks"

This kit is implemented in C++. Its source code can be compiled 
with GCC, by using command make in the software directory. 

The software of the filter is released with the GNU General Public 
License; please consult the file COPYING.txt for more information. 

*********************************************************************
**** THIS SOFTWARE IS A RESEARCH PROTOTYPE AND IS PROVIDED WITH *****
****    ABSOLUTELY NO GUARANTEE AND ABSOLUTELY NO SUPPORT!      *****
*********************************************************************

Usage
=====

After compilation an executable with the name HEMKit will be created
in the bin folder. This executable needs the following arguments in 
order to run:

- argv[1] is a text file containing the hierarchy. The hierarchy file contains 
the hierarchy information about the categories of the train set. Each line 
of this file is a relation between a parent and a child node. For example, 
the line:

10 20

is to be read as node 10 is parent of node 20. 

- argv[2] is a text file containing in each line the true categories of an 
instance separated by spaces. For example:

1 2 3
1 4 5
2 4

is two be read as first instance belongs to categories 1, 2 and 3
second instance belongs to categories 1, 4 and 5
and third instance belongs to categories 2 and 4.

- argv[3] is a text file containing in each line the predicted categories of an 
instance separated by spaces. For example:

1
2 4
2 4

is two be read as first instance is predicted to belongs to category 1
second instance is predicted to belong  to categories 2 and 4
third instance is predicted to belong  to categories 2 and 4.

- Argo[4] specifies the maximum distance that the measures will search in order 
to link nodes. Above that threshold all nodes will be considered to have a 
common ancestor. For example if a value of 1 is used then all nodes are considered 
to have a dummy common ancestor as direct parent of them. This option should 
usually be set to a very large number (for example 100000). But in very large 
datasets it should be set to values like 2 or 3 for computational reasons (see 
paper for further details).

- Argo[5] specifies the maximum error with which pair-based measures penalize
nodes that were matched with a default one (see paper for further details).

- Argo[6] should only be used in order to create an auxiliary files for 
conducting significance tests. This file contains the results of each evaluation 
measure for each instance (not averaged for all instances). If this argument is 
used it should be filled with a file name which in the end will contain the above 
information.

Example:
After running make to the software folder, go to example files folder and execute 
the following command:

./bin/HEMKit cat_hier.txt Golden.txt result.txt 100000 5

This will use the cat_hier.txt file as a hierarchy,
the Golden.txt as the file containing the true categories per instance,
the result.txt as the file containing the predicted categories per instance,
will not use any dummy common ancestors since 100000 is a very large number 
compared to the hierarchy, and will penalize nodes that were matched with a 
default one with 5. Since no argv[6] is used, no extra file will be created.
The results of each measure will be printed to the standard output.
Source code
===========

The source code can be found in the following directory:

software sub-directory

Copyright (c) 2013 A. Kosmopoulos.
Please send bug reports to Aris Kosmopoulos <akosmo@iit.demokritos.gr>.

------ END OF FILE ------

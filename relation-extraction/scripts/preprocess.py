import sys
import os

datapath = sys.argv[1] # data path
datatype = sys.argv[2] # train or test

inputList = list()

with open(datapath, 'r') as dataFilePt:
    for line in dataFilePt.readlines():
        column = line.splitlines()[0].split('\t')
        inputList.append(column)

for idx, row in enumerate(inputList):
    if idx==0 and datatype=="train":
        print("sentence\tlabel")
        
    assert len(row) == len(inputList[0]), "Error: the number of column element inconstant"
    
    if datatype=="test":
        print("\t".join(row[:2])) # only index and sentence
    else:
        print("\t".join(row))

 

/* 
 * File:   distanceCopmuter.h
 * Author: aris
 *
 * Created on November 28, 2011, 1:16 PM
 */

#ifndef DISTANCECOPMUTER_H
#define	DISTANCECOPMUTER_H
#include "tools.h"
#include "graph.h"
#include "subGraphCreator.h"
#include "bestPathfinder.h"
using namespace std;

class DistanceComputer {
    Graph* G;
    set<int> pr;
    set<int> gs;
    Graph gsG;
    Graph prG;
    int maxError;
    BestPathfinder* bp;
public:    
    DistanceComputer (Graph& G, set<int>& pr, set<int>& gs,int max, int maxError) {
        this->G = &G;
        this->pr = pr;
        this->gs = gs;
	this->maxError = maxError;
        bp = new BestPathfinder (G,gs,pr,max);		                
//        Coder c;
//        gsG.printGraph(c);
//        cout << endl;
//        prG.printGraph(c);                
    }
    double getMGIA (){
      set<int> uni = addSets(pr,gs);
      set<int> inter = getIntrOfSets(pr,gs);
      double subsetSize = getSubsetMinusCommon(uni,inter).size();
      double res = bp->computeMGIA (maxError,inter);      
      
      if (subsetSize == 0)
	    return 1.0;
      else {	
	double mult =  subsetSize*maxError; 		
	return (( mult - res)/ mult);
      }
    }
    /*
    unsigned getDistance () {
        unsigned ret = 0;         
        map<int,Node*>& gsMap = gsG.getG();
        map<int,Node*>& prMap = prG.getG();
        
        map<int,Node*>::iterator iter;      
        unsigned value;
        set<int>::iterator s_iter1, s_iter2;
        	        
	
	for (iter = gsMap.begin(); iter != gsMap.end(); iter++) {
            s_iter1 = gs.find(iter->first);
            s_iter2 = pr.find(iter->first);
            if (s_iter1 == gs.end() || s_iter2 == pr.end()) {
                if (prG.contains(iter->first) == false) {
                    value = 1.0;
                    ret += value;
                }
            }
        }
        for (iter = prMap.begin(); iter != prMap.end(); iter++) {
            s_iter1 = gs.find(iter->first);
            s_iter2 = pr.find(iter->first);
            if (s_iter1 == gs.end() || s_iter2 == pr.end()) {
                if (gsG.contains(iter->first) == false) {
                    value = 1.0;
                    ret += value;
                }
            }
        }
        return ret;
    }
    */
    double getF (double& precision, double& recall ) {
	bp->createSubGraphs (gsG,prG);
        map<int,Node*>& gsMap = gsG.getG();
        map<int,Node*>& prMap = prG.getG();
        
        map<int,Node*>::iterator iter;      
        unsigned value;
        set<int>::iterator s_iter1, s_iter2;
	set<int> AncsT;
	set<int> AncsP;
         	        
	
	for (iter = gsMap.begin(); iter != gsMap.end(); iter++) {
            AncsT.insert(iter->first);
        }
        for (iter = prMap.begin(); iter != prMap.end(); iter++) {
            AncsP.insert(iter->first);
        }
        set<int> PTInter = getIntrOfSets (AncsP,AncsT);
	precision = (PTInter.size()/((double) AncsP.size()));            
        recall = (PTInter.size()/((double) AncsT.size()));
	double F1 = 0.0;
        if (precision != 0.0 || recall != 0.0)
	  F1 = ((2*precision*recall)/(precision+recall));
        return F1;		
    }
    ~DistanceComputer(){delete bp;}
};

#endif	/* DISTANCECOPMUTER_H */


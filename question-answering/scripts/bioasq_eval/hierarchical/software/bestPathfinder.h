/* 
 * File:   bestPathfinder.h
 * Author: aris
 *
 * Created on November 16, 2011, 4:39 PM
 */

#ifndef BESTPATHFINDER_H
#define	BESTPATHFINDER_H
#include "graph.h"
#include "path.h"
#include "greatPath.h"
#include <utility>
#include <algorithm> 
#include "matches.h"
using namespace std;
class sum_pair {
  int sum;
public:
  pair<Path,Path> p;  
  sum_pair(pair<Path,Path>& p) {
    this->p = p;    
    sum = p.first.getSum() + p.second.getSum();
  }
  bool operator<( const sum_pair& val ) const {      
	return (sum < val.sum);       
    }
};
class BestPathfinder {
private:
    Graph* fullG;
    set<int> trueNodes;
    set<int> predictedNodes;
    set<int>::iterator s_iter;
    map<int,vector<Path> > pathsPerNode;
    map<int,vector<Path> >::iterator iter;
    vector<sum_pair> getBestPath(int initialNode,GreatPath& gp);
    vector<int> getBestPairs(int initialNode,GreatPath& gp, int& dist);
    int max;
public:
    BestPathfinder(Graph& fullG, set<int>& tNodes, set<int>& pNodes, int max);
    BestPathfinder(const BestPathfinder& orig);
    virtual ~BestPathfinder();
            
    void createSubGraphs (Graph& gsG, Graph& prG);
    double computeMGIA (int maxError, set<int>& intersection);
    void printPathsPerNode();    
};

#endif	/* BESTPATHFINDER_H */


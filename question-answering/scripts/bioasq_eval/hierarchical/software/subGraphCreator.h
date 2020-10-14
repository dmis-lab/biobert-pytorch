/* 
 * File:   subGraphCreator.h
 * Author: aris
 *
 * Created on November 10, 2011, 12:34 PM
 */

#ifndef SUBGRAPHCREATOR_H
#define	SUBGRAPHCREATOR_H
#include "graph.h"
using namespace std;
class SubGraphCreator {
    Graph* fullG;     
    Graph* trueG;
    Graph* predictedG;
    
public:    
    SubGraphCreator(Graph& fullG, Graph& trueG, Graph& predictedG);
    SubGraphCreator(const SubGraphCreator& orig);
    virtual ~SubGraphCreator();
    
    void createSubGraph ();
};

#endif	/* SUBGRAPHCREATOR_H */


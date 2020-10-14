/* 
 * File:   subGraphCreator.cpp
 * Author: aris
 * 
 * Created on November 10, 2011, 12:34 PM
 */

#include "subGraphCreator.h"

SubGraphCreator::SubGraphCreator(Graph& fullG, Graph& trueG, Graph& predictedG) {
    this->fullG = &fullG;    
    this->trueG = &trueG;
    this->predictedG = &predictedG;        
}

SubGraphCreator::SubGraphCreator(const SubGraphCreator& orig) {
    fullG = orig.fullG;    
    trueG = orig.trueG;
    predictedG = orig.predictedG;
}

SubGraphCreator::~SubGraphCreator() {
}

//void SubGraphCreator::createSubGraph () {
//    set<int> gsNodes, prNodes;
//    set<int>::iterator s_iter1,s_iter2;
//    map<int,Node*>::iterator m_iter;
//    //make sets with initial nodes
//    for (m_iter=trueG->getG().begin();m_iter!=trueG->getG().end();m_iter++)
//        gsNodes.insert(m_iter->first);
//    for (m_iter=predictedG->getG().begin();m_iter!=predictedG->getG().end();m_iter++)
//        prNodes.insert(m_iter->first);
//    //make sets with finded ancestors on the other sides
//    map<int,int> nodeToAncestor;
//    map<int,int>::iterator anc_iter;
//    unsigned matchesToFind = gsNodes.size() + prNodes.size();
//    for (s_iter1=gsNodes.begin();s_iter1!=gsNodes.end();s_iter1++) {
//        int node = *s_iter1;
//        s_iter2 = prNodes.find(node);
//        if (s_iter2!=prNodes.end()) {
//            matchesToFind -= 2;
//            nodeToAncestor[node] = node;            
//        }
//    }
//    set<int> oldGsNodes = gsNodes;
//    set<int> oldPrNodes = prNodes;
//    while (matchesToFind != 0) {
//        set<int> newGsNodes, newPrNodes;
//        set<int>::iterator n_iter1,n_iter2;
//        for (n_iter1=oldGsNodes.begin();n_iter1!=oldGsNodes.end();n_iter1++) {//add new ancestors to trueG
//            if (*n_iter1 != 0) {//for each node in forhead
//                Node* fullGNode = fullG->getG()[*n_iter1];
//                set<Node*>& ancestors = fullGNode->getParents();
//                set<Node*>::iterator anc_iter;
//                for (anc_iter=ancestors.begin();anc_iter!=ancestors.end();anc_iter++) {//for each ancestor of the node
//                    int nodeId = (*anc_iter)->getId();
//                    if (trueG->contains(nodeId) == false) {//if not a circle
//                        Node* node;
//                        if (!trueG->contains(nodeId)) {//if it is not contained in the graph
//                            newGsNodes.insert(nodeId);                                                
//                            node = new Node(nodeId);                        
//                            (*trueG)[nodeId] = *node;
//                        }
//                        else {
//                            node = &((*trueG)[nodeId]);
//                        }
//                        (*trueG)[(*n_iter1)].addParent(node);
//                    }
//                }
//            }
//        }
//        
//        for (n_iter1=oldPrNodes.begin();n_iter1!=oldPrNodes.end();n_iter1++) {//add new ancestors to predictedG
//            if (*n_iter1 != 0) {//for each node in forhead
//                Node* fullGNode = fullG->getG()[*n_iter1];
//                set<Node*>& ancestors = fullGNode->getParents();
//                set<Node*>::iterator anc_iter;
//                for (anc_iter=ancestors.begin();anc_iter!=ancestors.end();anc_iter++) {//for each ancestor of the node
//                    int nodeId = (*anc_iter)->getId();
//                    if (predictedG->contains(nodeId) == false) {//if not a circle
//                        Node* node;
//                        if (!predictedG->contains(nodeId)) {//if it is not contained in the graph
//                            newPrNodes.insert(nodeId);                                                
//                            node = new Node(nodeId);                        
//                            (*predictedG)[nodeId] = *node;
//                        }
//                        else {
//                            node = &((*predictedG)[nodeId]);
//                        }
//                        (*predictedG)[(*n_iter1)].addParent(node);
//                    }
//                }
//            }
//        }
//        for (n_iter1 = gsNodes.begin();n_iter1 != gsNodes.end();n_iter1++) {//search for common ancestor
//            int node = *n_iter1;
//            anc_iter = nodeToAncestor.find(node);
//            if (anc_iter == nodeToAncestor.end()) {//if not found
//                set<int> ancestors = trueG->getAncestorsOfNode(node);
//                for (n_iter2 = ancestors.begin();n_iter2 != ancestors.end();n_iter2++) {
//                    int anc = *n_iter2;
//                    if (predictedG->contains(anc)){
//                        nodeToAncestor[node] = anc;
//                        matchesToFind -= 1;
//                        break;
//                    }
//                }
//            }
//        }
//        
//        for (n_iter1 = prNodes.begin();n_iter1 != prNodes.end();n_iter1++) {//search for common ancestor
//            int node = *n_iter1;
//            anc_iter = nodeToAncestor.find(node);
//            if (anc_iter == nodeToAncestor.end()) {//if not found
//                set<int> ancestors = predictedG->getAncestorsOfNode(node);
//                for (n_iter2 = ancestors.begin();n_iter2 != ancestors.end();n_iter2++) {
//                    int anc = *n_iter2;
//                    if (trueG->contains(anc)){
//                        nodeToAncestor[node] = anc;
//                        matchesToFind -= 1;
//                        break;
//                    }
//                }
//            }
//        }        
//        oldGsNodes = newGsNodes;
//        oldPrNodes = newPrNodes;
//    }//I should have one8 common ancestor for each node
//    
//}
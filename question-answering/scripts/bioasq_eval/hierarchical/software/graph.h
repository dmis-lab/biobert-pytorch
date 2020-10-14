/* 
 * File:   graph.h
 * Author: aris
 *
 * Created on November 9, 2011, 3:52 PM
 */

#ifndef GRAPH_H
#define	GRAPH_H
#include "node.h"
#include "tools.h"
#include <map>
#include <string>
#include <fstream>
#include <iostream>
#include "path.h"
#include "coder.h"
#include <algorithm>
#include "NodeLength.h"
using namespace std;

class Graph {        
    map<int,int> depthNode;    
    //set<int> topParents;  
    set<int> getParentsOfNode (int n);
    void addAncestor (set<int>& s,int n);
    void getAllAncestorsByDepthFornextDepth (set<int>& used, vector<NodeLength>& ret, set<int>& next,double depth);
    
    int depthOfNodeFromZero (int node);
    
    void addAncestor (vector<Path>& paths, set<int> s, vector<int> path, int n, int curLength, int max);
    
    void addAncestor (vector<Path>& paths, set<int> s, vector<int> path, int n);
    
    void getAllAncestorsFornextDepth (set<int>& ret, set<int>& next,int depth, int maxDepth);
public:
    map<int, Node*> G;
    map<int, Node*>::iterator iter;
    
    Graph (){}
    Graph (string fileName);
    Graph (set<int> nodes, Graph& Gr);    
    ~Graph ();
    
    bool isLeaf (int i) {
        Node* node = G[i];
        set<Node*> children = node->getChildren();
        if (children.size() == 0) return true; else return false;        
    }
    
    bool isParentOfLeaf (int i) {
        Node* node = G[i];
        set<Node*> children = node->getChildren();
        for (set<Node*>::iterator iter = children.begin();iter!=children.end();iter++) {
            int id = (*iter)->getId();
            if (isLeaf(id))
                return true;
        }
        return false;
    }
    void deleteNode (int i);
    
    void removeCycles();
    
    void printG (string fileName);
    
    void printG ();
    
    void computeDepthOfEachNode() {
        for(iter=G.begin();iter!=G.end();iter++) 
            depthNode[iter->first] = depthOfNodeFromZero(iter->first);                    
    }    
    Node* getNode(int id) {
        return G[id];
    }
    Node& operator[] (int i){
        iter = G.find(i);
        if (iter == G.end()) {
            Node* ret;// = new Node(i);
            G[i]=ret;
            return *ret;
        }
        else
            return *(iter->second);
    }
    
    void printTopParents() {
        set<Node*>::iterator s_iter;
        cout << "Number nodes = " << G.size() << endl;        
        set<Node*> s = G[0]->getChildren();
        cout << "Number top Parents = " << s.size() << endl;
        for (s_iter = s.begin();s_iter != s.end();s_iter++) {
            cout << *s_iter << " ";
        }
        cout << endl;
    }
    
    void fixTopParents();
    
    void fixConnections(Graph& Gr);
    
    map<int, Node*>& getG () {
        return G;
    }
    
    bool contains (int id) {
        iter = G.find(id);
        if (iter == G.end())
            return false;
        else
            return true;
    }
    
    void addNode (int n) {
        iter = G.find(n);
        if (iter == G.end())
            G[n] = new Node(n);
    }
    set<int> getAncestorsOfNode(int n);
    
    vector<Path> getAncestralPathsOfNode(int n, int max);
            
    void addPath (Path& p);
    
    void printGraph(Coder& c,int max);
    void printSize() {
        cout << "# of nodes = " << G.size() << endl;
    //    cout << "node = " << G.begin()->first << endl;
    }
    
    vector<NodeLength> getAllAncestorsByDepth (int n);
    
    int computeDeepestNode ();
    
    set<int> getAllAncestors (int n, int maxDepth=100);
    
    void getAllAncestors (set<int>& leaves);            
};

#endif	/* GRAPH_H */


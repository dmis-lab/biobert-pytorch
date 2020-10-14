/* 
 * File:   path.h
 * Author: aris
 *
 * Created on November 16, 2011, 3:35 PM
 */

#ifndef PATH_H
#define	PATH_H
#include <set>
#include <vector>
#include "coder.h"
using namespace std;
class Path {
    set<int>::iterator sf_iter;
public:
    bool predNotG;
    set<int> nodes;
    vector<int> path;    
    Path(){}    
    Path (set<int>& nodes,vector<int>& path) {
        this->nodes = nodes;
        this->path = path;
    }
    Path (const Path& p) {
        nodes = p.nodes;
        path = p.path;
	predNotG = p.predNotG;
    }    
    Path& operator=(const Path& p){
        if (this == &p)
            return *this;
        nodes = p.nodes;
        path = p.path;
	predNotG = p.predNotG;
        return *this;
    }
    int getSum () {
      int ret= 0;
      for (int i=0;i<path.size();i++)
	ret+=path[i];
      return ret;
    }
    
    int getHead () {
      return path[0];
    }
    int size() {
      return nodes.size();
    }
    void addDummyNode() {
        nodes.insert(-1);
        path.push_back(-1);
    }
    void fixPath (vector<int>& path, int node) {
        nodes.clear();
        this->path.clear();
        for (int i=0;i<path.size();i++) {
            int n = path[i];
            this->path.push_back(n);
            nodes.insert(n);
            if (n == node)
                break;
        }            
    }
    void print() {
        for (int i=0;i<path.size();i++)
            cout << path[i] << " ";
        cout << endl;
    }
    void print(Coder& c) {
        for (int i=0;i<path.size();i++)
            cout << c.getCode(path[i]) << " ";
        cout << endl;
    }
    bool contains (int n) {
        sf_iter = nodes.find(n);
        if (sf_iter == nodes.end())
            return false;
        else
            return true;
    }
    int getLength (int n) {        
        for(int i=0;i<path.size();i++) {
            if (n==path[i]) {
                return (i);
            }
        }
    }
    
     static vector<Path> clearUnnecessaryPaths (vector<Path>& v) {
        vector<Path> ret;
        set<int>::iterator s_iter;
        int size = v.size();        
        set<int> leaves, moreThan1Leaves;        
        set<int> ans, moreThan1Ans;
        for (int i=0;i<size;i++) {
            vector<int>& p = v[i].path;
            int node = p[0];
            s_iter = leaves.find(node);
            if (s_iter == leaves.end())
                leaves.insert(node);
            else
                moreThan1Leaves.insert(node);//appears more than once as a leaf
            node = p[p.size()-1];
            s_iter = ans.find(node);
            if (s_iter == ans.end())
                ans.insert(node);
            else
                moreThan1Ans.insert(node);//the same for ancestor
        }
                
        for (int i=0;i<size;i++) {
            vector<int>& p = v[i].path;
            int node = p[0];
            s_iter = moreThan1Leaves.find(node);
            if (s_iter == moreThan1Leaves.end())
                ret.push_back(v[i]);
            else {
                node = p[p.size()-1];
                s_iter = moreThan1Ans.find(node);
                if (s_iter == moreThan1Ans.end())
                        ret.push_back(v[i]);
            }
        }
        return ret;
    }
};
#endif	/* PATH_H */


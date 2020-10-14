/* 
 * File:   greatPath.h
 * Author: aris
 *
 * Created on November 21, 2011, 1:10 PM
 */

#ifndef GREATPATH_H
#define	GREATPATH_H
#include "path.h"
class GreatPath {
    set<int>::iterator sf_iter;
public:
    set<int> allNodes;
    
    vector<Path> paths;
    GreatPath(){}
    GreatPath (set<int>& nodes,map<int,vector<Path> >& pathsPerNode) {        
        set<int>::iterator iter;
        for (iter=nodes.begin();iter!=nodes.end();iter++) {
            vector<Path>& v = pathsPerNode[*iter];
            for (int i=0;i<v.size();i++) {
                paths.push_back(v[i]);
                vector<int>& nodesInPath = v[i].path;
                for (int j=0;j<nodesInPath.size();j++)
                    allNodes.insert(nodesInPath[j]);
            }
        }
    }
    bool contains (int n) {
        sf_iter = allNodes.find(n);
        if (sf_iter == allNodes.end())
            return false;
        else
            return true;
    }
};

#endif	/* GREATPATH_H */


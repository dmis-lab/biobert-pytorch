/* 
 * File:   node.h
 * Author: aris
 *
 * Created on November 9, 2011, 3:44 PM
 */

#ifndef NODE_H
#define	NODE_H
#include <set>
using namespace std;
class Node {
    set<Node*> parents;
    set<Node*> children;
    int id;
public:
    Node (int id) {
        this->id = id;        
    }
        
    void addParent(Node* n){parents.insert(n);}
    
    void deleteParent (Node* n) {parents.erase(n);}
    
    void addChild(Node* n){children.insert(n);}
    
    void deleteChild(Node* n){children.erase(n);}
    
    set<Node*>& getParents () {
        return parents;
    }
    
    set<Node*>& getChildren () {
        return children;
    }
    int getId() {
        return id;
    }
};
#endif	/* NODE_H */


#include "graph.h"

Graph::Graph (string fileName) {
    ifstream fin (fileName.c_str());    
    map<int, Node*>::iterator iter_child;
    string line;
    while (getline(fin,line)) {
        if (line != "") {
                vector<string> ids = split(line);
                int parent = stringToInt(ids[0]);
                int child = stringToInt(ids[1]);
                iter = G.find(parent);
                if (iter == G.end()) {//if it exists
                    Node* parNode = new Node(parent);
                    G[parent] = parNode;
                    iter_child = G.find(child);
                    Node* chNode;
                    if (iter_child == G.end()) {
                        chNode = new Node(child);
                        G[child] = chNode;
                    }
                    else
                        chNode = iter_child->second;
                    parNode->addChild(chNode);
                    chNode->addParent(parNode);                    
                }
                else {//create a new node
                    Node* parNode = iter->second;
                    iter_child = G.find(child);
                    Node* chNode;
                    if (iter_child == G.end()) {        
                        chNode = new Node(child);
                        G[child] = chNode;
                    }
                    else
                        chNode = iter_child->second;
                    parNode->addChild(chNode);
                    chNode->addParent(parNode);
                }
        }
    }
    fin.close();
    fixTopParents();
}

Graph::Graph (set<int> nodes, Graph& Gr) {
    set<int>::iterator local_s_iter;
    for (local_s_iter=nodes.begin(); local_s_iter!= nodes.end();local_s_iter++) {
        int nodeId = *local_s_iter;        
        Node* newNode = new Node(nodeId);
        G[nodeId] = newNode;
    }
    fixConnections(Gr);
}
Graph::~Graph(){
    for (iter=G.begin();iter!=G.end();iter++)
        delete iter->second;
}

void Graph::fixTopParents(){
    //topParents.empty();
    Node* topNode = new Node(0);
    G[0] = topNode;
    int nodeId;
    Node* node;
    for (iter=G.begin();iter != G.end();iter++) {
        nodeId = iter->first;
        node = iter->second;
        if (node->getParents().size() == 0 && nodeId != 0) {
            node->getParents().insert(topNode);
            topNode->addChild(node);
        }
    }
}

void Graph::fixConnections(Graph& Gr){
    set<Node*>::iterator local_sn_iter;
    map<int,Node*>::iterator local_m_iter;
    for (iter=G.begin(); iter!= G.end();iter++) {
        int nodeId = iter->first;
        Node* node = iter->second;        
        Node& oldNode = Gr[nodeId];
        set<Node*> parents = oldNode.getParents();
        for (local_sn_iter = parents.begin();local_sn_iter != parents.end(); local_sn_iter++) {            
        
            local_m_iter = G.find((*local_sn_iter)->getId());
            if (local_m_iter != G.end()) {
                node->addParent(local_m_iter->second);
            }
        }
        set<Node*> children = oldNode.getChildren();
        for (local_sn_iter = children.begin();local_sn_iter != children.end(); local_sn_iter++) {
            local_m_iter = G.find((*local_sn_iter)->getId());
            if (local_m_iter != G.end()) {
                node->addChild(local_m_iter->second);
            }
        }
    }
}

set<int> Graph::getParentsOfNode (int n) {
    set<int> ret;    
    set<Node*>& ancestors = G[n]->getParents();
    set<Node*>::iterator sn_iter;
    for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
        ret.insert ((*sn_iter)->getId());        
    }
    return ret;
}

set<int> Graph::getAncestorsOfNode(int n) {
    set<int> ret;
    set<int>::iterator s_iter;    
    set<Node*>& ancestors = G[n]->getParents();
    set<Node*>::iterator sn_iter;
    for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
        int anc = (*sn_iter)->getId();
        s_iter = ret.find(anc);
        if (s_iter == ret.end())
            addAncestor(ret,anc);
    }
    return ret;
}

void Graph::addAncestor (set<int>& s,int n) {
    s.insert(n);
    set<int>::iterator s_iter;    
    set<Node*>& ancestors = G[n]->getParents();
    set<Node*>::iterator sn_iter;
    for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
        int anc = (*sn_iter)->getId();
        s_iter = s.find(anc);
        if (s_iter == s.end())
            addAncestor(s,anc);
    }
}

vector<Path> Graph::getAncestralPathsOfNode(int n,int max) {    
    vector<Path> paths;
    set<int>::iterator s_iter;    
    //set<Node*>& ancestors = G[n]->getParents();
    //set<Node*>::iterator sn_iter;
    //for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
      //  int anc = (*sn_iter)->getId();
    vector<int> path;        
    set<int> nodesUsed;                               
    addAncestor(paths, nodesUsed, path, n, 0, max);                        
    //}
    
//    fix the maximum distance problem
    for (int i=0;i<paths.size();i++) {
        Path& p = paths[i];
        p.addDummyNode();
    }    
    return paths;
}

void Graph::addAncestor (vector<Path>& paths, set<int> s, vector<int> path, int n, int curLength, int max) {
    path.push_back(n);
    s.insert(n);
    
    set<int>::iterator s_iter;    
    set<Node*>& ancestors = G[n]->getParents();
    set<Node*>::iterator sn_iter;
    if (ancestors.size() == 0 || curLength == max) {
        Path p (s,path);
        paths.push_back(p);
    }
    else {
        for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
            int anc = (*sn_iter)->getId();
            s_iter = s.find(anc);
            if (s_iter != s.end()) {
                Path p (s,path);
                paths.push_back(p);
            }                
            else 
                addAncestor(paths,s,path,anc, curLength+1, max);                                        
        }
    }
}

void Graph::addAncestor (vector<Path>& paths, set<int> s, vector<int> path, int n) {
    path.push_back(n);
    s.insert(n);
    
    set<int>::iterator s_iter;    
    set<Node*>& ancestors = G[n]->getParents();
    set<Node*>::iterator sn_iter;
    if (ancestors.size() == 0) {
        Path p (s,path);
        paths.push_back(p);
    }
    else {
        for (sn_iter = ancestors.begin(); sn_iter!=ancestors.end();sn_iter++) {
            int anc = (*sn_iter)->getId();
            s_iter = s.find(anc);
            if (s_iter != s.end()) {
                Path p (s,path);
                paths.push_back(p);
            }                
            else 
                addAncestor(paths,s,path,anc);                                        
        }
    }
}

void Graph::addPath (Path& p) {
    vector<int>& v = p.path; 
    if (v.size() == 1) {
      int nodeAnc = v[0];      
      iter = G.find(nodeAnc);
      Node* anc;      
      if (iter == G.end()) { 
	  anc = new Node (nodeAnc);
	  G[nodeAnc] = anc;
      }                 
    }
    else {
      for (int i=1;i<v.size();i++) {
	  int nodeAnc = v[i];
	  int nodeDec = v[i-1];
	  iter = G.find(nodeAnc);
	  Node* anc;
	  Node* dec;
	  if (iter == G.end()) { 
	      anc = new Node (nodeAnc);
	      G[nodeAnc] = anc;
	  }
	  else 
	      anc = iter->second;
	  iter = G.find(nodeDec);
	  if (iter == G.end()){
	      dec = new Node (nodeDec);
	      G[nodeDec] = dec;
	  }
	  else 
	      dec = iter->second;
	  anc->addChild(dec);
	  dec->addParent(anc);                
      }
    }
}

void Graph::printGraph(Coder& c,int max) {
    set<int> leaves;
    set<int>::iterator iter_leaves;
    
    for (iter=G.begin();iter!=G.end();iter++) {
        if (iter->second->getChildren().size() == 0)
            leaves.insert(iter->first);
    }
    for (iter_leaves=leaves.begin();iter_leaves!=leaves.end();iter_leaves++) {
        vector<Path> v = getAncestralPathsOfNode(*iter_leaves,max);
        for (int i=0;i<v.size();i++)
            v[i].print(c);
    }            
}

vector<NodeLength> Graph:: getAllAncestorsByDepth (int n) {
    vector<NodeLength> ret;
    set<int> used;
    set<int> ancs = this->getParentsOfNode(n);        
    getAllAncestorsByDepthFornextDepth (used,ret,ancs,0.0);
    sort (ret.begin(), ret.end());
    return ret;
}

void Graph::getAllAncestorsByDepthFornextDepth (set<int>& used, vector<NodeLength>& ret, set<int>& next,double depth) {
    set<int>::iterator used_iter, for_iter,for_iter2;
    set<int> nextNext;     
    
    for (for_iter=next.begin();for_iter!=next.end();for_iter++) {
        int node = *for_iter;                
        used.insert(node);       
        NodeLength nl (node,(depth + 1.0));
        ret.push_back(nl);
        set<int> ancs = this->getParentsOfNode(node);
        for (for_iter2=ancs.begin();for_iter2!=ancs.end();for_iter2++) {
            used_iter = used.find(*for_iter2);
            if (used_iter==used.end()) 
                    nextNext.insert(*for_iter2);
        }
        
    }            
    if (nextNext.size() != 0 && depth < 16)
        getAllAncestorsByDepthFornextDepth(used,ret,nextNext,(depth+1.0)); 
}

set<int> Graph:: getAllAncestors (int n, int maxDepth) {
    set<int> ret;    
    set<int> ancs = this->getParentsOfNode(n);        
    getAllAncestorsFornextDepth (ret,ancs,0,maxDepth);    

set<int>::iterator l_iter = ret.find(n);
  if (l_iter!=ret.end())
	    ret.erase(l_iter);

    return ret;
}

void Graph::getAllAncestorsFornextDepth (set<int>& ret, set<int>& next,int depth, int maxDepth) {
    set<int>::iterator used_iter, for_iter,for_iter2;
    set<int> nextNext;     
    
    for (for_iter=next.begin();for_iter!=next.end();for_iter++) {
        int node = *for_iter;                                      
        ret.insert(node);
        set<int> ancs = this->getParentsOfNode(node);
        for (for_iter2=ancs.begin();for_iter2!=ancs.end();for_iter2++) {
            used_iter = ret.find(*for_iter2);
            if (used_iter==ret.end()) 
                    nextNext.insert(*for_iter2);
        }
        
    }            
    if (nextNext.size() != 0 && depth < maxDepth)
        getAllAncestorsFornextDepth(ret,nextNext, depth+1,maxDepth); 
}


int Graph::depthOfNodeFromZero (int node) {
    int ret = 0;    
    set<Node*>& parents = G[node]->getParents();
    set<Node*> next = parents;
    set<Node*>::iterator n_iter, n_iter2;    
    set<int> used;
    set<int>::iterator used_iter;
    
    for (n_iter = next.begin();n_iter!=next.end();n_iter++) 
        used.insert((*n_iter)->getId());
    
    bool stop = true;
    while (stop) {        
        ret ++;
        //cout << ret <<  " !!!" << endl;
        set<Node*> tempNext;
        int node;
        for (n_iter = next.begin();n_iter!=next.end();n_iter++) {
            node = (*n_iter)->getId();            
            //cout << "node = " << node << endl;
            if (node == 0) {
                stop = false;
                break;
            }
            else {
                for (n_iter2 = (*n_iter)->getParents().begin();n_iter2 != (*n_iter)->getParents().end();n_iter2++) {
                    //cout << "  parent = " << (*n_iter2)->getId() << endl;
                    used_iter = used.find((*n_iter2)->getId());
                    if (used_iter == used.end()) {
                        tempNext.insert(*n_iter2);
                        used.insert((*n_iter2)->getId());
                    }
                }
            }            
        }        
        next = tempNext;
        if (next.size() == 0)//cycle
            break;
    }
    return ret;
}

int Graph::computeDeepestNode () {
    int ret = 0;
    int count = G.size();
    for (iter=G.begin();iter!=G.end();iter++) {
        if (count % 10000 == 0)
                cout << count << "node : " <<  iter->first << endl;
        count--;
        if (iter->second->getChildren().size() == 0) {
            int d = depthOfNodeFromZero(iter->first);
            if (ret < d)
                ret = d;
        }
    }
    //int d = depthOfNodeFromZero(9938);
    return ret;
}

void Graph::deleteNode (int i) {
        iter = G.find(i);
        Node* p = iter->second;
        set<Node*>& parents = p->getParents();
        set<Node*>::iterator s_iter;
        for (s_iter=parents.begin();s_iter!=parents.end();s_iter++)
            (*s_iter)->deleteChild(p);
        set<Node*>& children = p->getChildren();
        for (s_iter=children.begin();s_iter!=children.end();s_iter++)
            (*s_iter)->deleteParent(p);
        delete p;
        G.erase(iter);
}

void Graph::removeCycles() {
    if (depthNode.size()==0)
        computeDepthOfEachNode();
    
    for (iter=G.begin();iter!=G.end();iter++) {
        set<Node*>& parents = iter->second->getParents();
        set<Node*>::iterator s_iter;
        int depth = depthNode[iter->first];
        set<Node*> parentsToBeRemoved;
        for (s_iter=parents.begin();s_iter!=parents.end();s_iter++) {
            int parentDepth = depthNode[(*s_iter)->getId()];
            if (depth <= parentDepth) {
                parentsToBeRemoved.insert(*s_iter);
                (*s_iter)->deleteChild(iter->second);
            }
        }
        for (s_iter=parentsToBeRemoved.begin();s_iter!=parentsToBeRemoved.end();s_iter++)
            parents.erase(*s_iter);
    }
}

void Graph::printG (string fileName) {
    deleteNode(0);
    ofstream fout (fileName.c_str());
    for (iter=G.begin();iter!=G.end();iter++) {
        set<Node*>& children = iter->second->getChildren();
        set<Node*>::iterator s_iter;
        int node = iter->first;
        for (s_iter=children.begin();s_iter!=children.end();s_iter++)
            fout << node << " " << (*s_iter)->getId() << endl;
    }        
    fout.flush();
    fout.close();
}

void Graph::printG () {
    //deleteNode(0);    
    for (iter=G.begin();iter!=G.end();iter++) {
        set<Node*>& children = iter->second->getChildren();
        set<Node*>::iterator s_iter;
        int node = iter->first;
	cout << node << " with children :" << endl;
        for (s_iter=children.begin();s_iter!=children.end();s_iter++)
            cout << "       " << (*s_iter)->getId() << endl;
    }
    //fixTopParents();
}

void Graph::getAllAncestors (set<int>& leaves) {
    set<int> ret;
    set<int>::iterator s_iter,s_iter2;
    for (s_iter=leaves.begin();s_iter!=leaves.end();s_iter++) {
        set<int> temp = getAllAncestors(*s_iter);       
        for (s_iter2=temp.begin();s_iter2!=temp.end();s_iter2++)
            ret.insert(*s_iter2);
    }
    for (s_iter=ret.begin();s_iter!=ret.end();s_iter++) 
        leaves.insert(*s_iter);
}

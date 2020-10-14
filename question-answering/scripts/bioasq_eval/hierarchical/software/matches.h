#ifndef _MATCHES_H_
#define _MATHCES_H_
#include <algorithm>
#include <map>
#include <vector>
#include <set>
#include <iostream>
#include "path.h"
#include "graph.h"
using namespace std;
class Match {
public:
  int pred;
  int golden;
  int length;
  bool removed;
  Match (int p,int g, int l):pred(p),golden(g),length(l){removed=false;}
  bool operator<( const Match& val ) const {
    if (length != val.length)
      return length > val.length; 
    else if (pred != val.pred)
      return pred > val.pred;
    else
      return golden > val.golden;
  }
  void print(){
	cout<< pred << " " << golden << " " << length << endl;
  }
};

class MatchCollection {
public:
  map <int , set<int> > predictedToGolden;
  map <int , set<int> > goldenToPredicted;
  map <int , set<int> >::iterator iter;
  set<int>::iterator s_iter;
  vector<Match> allMatches;  
  MatchCollection(){}
  
  int computeError () {
    int ret=0;
    for (int i=0;i<allMatches.size();i++) {
      Match& m= allMatches[i];
      if (m.removed==false)
	ret+=m.length;
    }
    return ret;
  }
  
  void removeMatch(int loc){    
    Match& m = allMatches[loc];
    m.removed = true;
    int p = m.pred;
    int g = m.golden;
    predictedToGolden[p].erase(g);
    goldenToPredicted[g].erase(p);
  }
  
  void addMatch(int p,int g, int l){
    bool includedInGOfP = false;
    bool includedInPOfG = false;
    iter = predictedToGolden.find(p);    
    if (iter==predictedToGolden.end()){
      set<int> s;
      s.insert(g);
      predictedToGolden[p]=s;
    }
    else{
      s_iter = iter->second.find(g);
      if (s_iter==iter->second.end())
	iter->second.insert(g);
      else {	
	includedInGOfP=true;
      }
    }
    iter = goldenToPredicted.find(g);
    if (iter==goldenToPredicted.end()){
      set<int> s;
      s.insert(p);
      goldenToPredicted[g]=s;
    }
    else{
      s_iter = iter->second.find(p);
      if (s_iter==iter->second.end())
	iter->second.insert(p);
      else {	
	includedInPOfG=true;
      }
    }
    if (!(includedInGOfP == true && includedInPOfG == true)) {
	Match m (p,g,l);
	allMatches.push_back(m);    
    }              
  }  
  void removeExtraMatches() {
      sort (allMatches.begin(),allMatches.end());
      //for (int i=0;i<allMatches.size();i++)
	//allMatches[i].print();
      //bool nothingToRemove=false;
      //while(nothingToRemove==false) {
	//nothingToRemove=true;
	for (int i=0;i<allMatches.size();i++) {
	  Match& m = allMatches[i];
	  int p = m.pred;
	  int g = m.golden;
	  if (predictedToGolden[p].size()>1){
	    if (goldenToPredicted[g].size()>1) //{
	      removeMatch(i);
	      //nothingToRemove=false;
	  //    break;
	    //}	      
	  }	    
	}
      //}
  }  
};
class AncestorLinks {
  class pairsOfAncImportance {
  public:
    int a;
    int imp;
    bool removed;
    pairsOfAncImportance (int aIn, int impIn):a(aIn), imp(impIn) {removed=false;}
    bool operator<( const pairsOfAncImportance& val ) const {
      if (imp != val.imp)
	return (imp > val.imp); 
      else 
	return (a > val.a);    
    }
  };
private:
  map<int,set <int> > ancestorsToSelectedNodes;
  map<int,set <int> > selectedNodesToAncs;
  map<int,set <int> >::iterator sn_iter;
  map<int, map<int, Path> > ancestorsToPaths;
  map<int, map<int, Path> >::iterator p_iter;  
  set<int>::iterator s_iter;
  map<int, Path>::iterator m_iter;
  vector<pairsOfAncImportance> ancs;
  set<int> goldenNodes;
  set<int> predictedNodes;
  //set<int> goldenGNodes;
  //set<int> predictedGNodes;  
public:
  
  
  AncestorLinks(set<int>& g, set<int> p,Graph& gsG, Graph& prG):goldenNodes(g),predictedNodes(p) {
    
    for (s_iter=g.begin();s_iter!=g.end();s_iter++) {
      goldenNodes.insert(*s_iter);      
      gsG.addNode(*s_iter);
    }
    for (s_iter=p.begin();s_iter!=p.end();s_iter++){
      predictedNodes.insert(*s_iter);
      prG.addNode(*s_iter);
    }
  }
  
  int getCommonNodes (Path& pr_path,Path& go_path,int& sumForRaceConditions) {
    int ret=0;
    for (int i=0;i<pr_path.path.size();i++) {
      sumForRaceConditions += pr_path.path[i];
      s_iter = predictedNodes.find(pr_path.path[i]);
      if (s_iter!=predictedNodes.end())
	ret++;
    }
    for (int i=0;i<go_path.path.size();i++) {
      sumForRaceConditions += go_path.path[i];
      s_iter = goldenNodes.find(go_path.path[i]);
      if (s_iter!=goldenNodes.end())
	ret++;
    }
    return ret;
  }
  
  int getCommonNodes (Path& p,set<int>& s) {
    int ret=0;
    for (int i=0;i<p.path.size();i++) {
      
      s_iter = s.find(p.path[i]);
      if (s_iter!=s.end())
	ret++;
    }    
    return ret;
  }
  
  void addPath (int anc, int pre, Path& pr_path, int gol, Path& go_path) {
    p_iter= ancestorsToPaths.find(anc);
    goldenNodes.insert(gol);
    predictedNodes.insert(pre);
    if (p_iter==ancestorsToPaths.end()) {
	map<int, Path> p;
	p[pre] = pr_path;
	p[gol] = go_path;	
	ancestorsToPaths[anc] = p;
    }
    else {
      map<int, Path>& p = p_iter->second;
	m_iter = p.find(pre);
	if (m_iter==p.end())
	  p[pre] = pr_path;
	/*
	else {
	  bool noDiff = true;
	  Path& pOld = m_iter->second;	  
	  int i=1;
	  int maximum = pOld.path.size()-1;
	  while (noDiff) {
	    int a = pOld.path[i];
	    int b = pr_path.path[i];
	    if (a < b) {//forRaceCond
	      p[pre] = pr_path;
	      noDiff = false;
	    }
	    i++;
	    if (i >= maximum)
	      noDiff = false;
	  }
	}*/
	m_iter = p.find(gol);
	if (m_iter==p.end()) {
	  p[gol] = go_path;   
	}/*
	else {
	  bool noDiff = true;
	  Path& pOld = m_iter->second;	  
	  int i=1;
	  int maximum = pOld.path.size()-1;
	  while (noDiff) {
	    int a = pOld.path[i];
	    int b = go_path.path[i];
	    if (a < b) {//forRaceCond
	      p[gol] = go_path;
	      noDiff = false;
	    }
	    i++;
	    if (i >= maximum)
	      noDiff = false;
	  }
	}*/
    }
  }
      
  void addAncAndTargets (int anc, int pre, int gol){
    sn_iter = ancestorsToSelectedNodes.find(anc);                
    if (sn_iter == ancestorsToSelectedNodes.end()) {
	set<int> s;
	s.insert(pre);
	s.insert(gol);
	ancestorsToSelectedNodes[anc] = s;
			
    }
    else {
      sn_iter->second.insert(pre);
      sn_iter->second.insert(gol);      
    }
    selectedNodesToAncs[pre].insert(anc);
    selectedNodesToAncs[gol].insert(anc);
  }
  
  set<int> selectBestAncs (set<int>& inter,int satisfactionsToResolve) {
    set<int> satisfiedNodesPr;
    set<int> satisfiedNodesGo;
    for (s_iter=inter.begin();s_iter!=inter.end();s_iter++) {
      satisfiedNodesPr.insert(*s_iter);      
      satisfiedNodesGo.insert(*s_iter);            
    }
    
    for (sn_iter=ancestorsToSelectedNodes.begin();sn_iter!=ancestorsToSelectedNodes.end();sn_iter++){
      int imp = sn_iter->second.size();
      pairsOfAncImportance poai (sn_iter->first,imp);
      ancs.push_back(poai);    
    }
    
    sort (ancs.begin(),ancs.end());
    //delete ancestors that are not important
    for (int i=0;i<ancs.size();i++) {
	set<int>& s = ancestorsToSelectedNodes[ancs[i].a];
	int countTimesWithMoreThanOne=0;
	for (s_iter=s.begin();s_iter!=s.end();s_iter++) {
	  if (selectedNodesToAncs[*s_iter].size() != 1)
	    countTimesWithMoreThanOne++;
	}
	if (countTimesWithMoreThanOne==s.size()){//remove
	  ancs[i].removed=true;
	  for (s_iter=s.begin();s_iter!=s.end();s_iter++) {
	    selectedNodesToAncs[*s_iter].erase(ancs[i].a);
	  }
	}
    }
    set<int> ret;
    set<int>::iterator local_s_iter;
    for (int i=0;i<ancs.size();i++) {
      if ((satisfiedNodesPr.size()+ satisfiedNodesGo.size())== satisfactionsToResolve)
	break;
      if (ancs[i].removed == true)
	continue;
      int anc = ancs[i].a;
      set <int> affectedNodes = ancestorsToSelectedNodes[anc];
      for (s_iter=affectedNodes.begin();s_iter!=affectedNodes.end();s_iter++) {
	int node = *s_iter;
	local_s_iter=goldenNodes.find(node);
	if (local_s_iter!=goldenNodes.end()) {
	    local_s_iter=satisfiedNodesGo.find(node);
	    if (local_s_iter==satisfiedNodesGo.end()) {
	      ret.insert(anc);
	      goldenNodes.insert(anc);
	      satisfiedNodesGo.insert(node);
	    }
	}
	local_s_iter=predictedNodes.find(node);
	if (local_s_iter!=predictedNodes.end()) {
	  local_s_iter=satisfiedNodesPr.find(node);
	    if (local_s_iter==satisfiedNodesPr.end()) {
	      ret.insert(anc);
	      predictedNodes.insert(anc);
	      satisfiedNodesPr.insert(node);
	    }
	}		
      }      
    }
    return ret;
  }
    
  void selectBestPaths (Graph& gsG, Graph& prG,set<int>& inter,int satisfactionsToResolve, set<int>& ancestors){    
    set<int> satisfiedNodesPr;
    set<int> satisfiedNodesGo;
    for (s_iter=inter.begin();s_iter!=inter.end();s_iter++) {
      satisfiedNodesPr.insert(*s_iter);      
      satisfiedNodesGo.insert(*s_iter);
      //gsG.addNode(*s_iter);
      //prG.addNode(*s_iter);
      selectedNodesToAncs[*s_iter].insert(*s_iter);
    }
    for (set<int>::iterator anc_iter=ancestors.begin();anc_iter!=ancestors.end();anc_iter++) {      
      if ((satisfiedNodesPr.size()+ satisfiedNodesGo.size())== satisfactionsToResolve)
	break;           
            
      int anc = *anc_iter;      
      map<int, Path>& nodesAffected = ancestorsToPaths[anc];
      
      int leftLinks = 0;
      Path* smallestLeftPath;
      int smallestLeftPathSize = 10000;
      
      int rightLinks = 0;            
      Path* smallestRightPath;
      int smallestRightPathSize = 10000;
      
      for (m_iter=nodesAffected.begin();m_iter!=nodesAffected.end();m_iter++) {
	int node = m_iter->first;		
	Path& p = m_iter->second;	
	int size = p.size();
	bool doNext = true;
	
	if (p.predNotG) {
	  s_iter = satisfiedNodesPr.find(node);
	  if (s_iter!=satisfiedNodesPr.end()) {
	    doNext=false;
	  
	    if (size < smallestLeftPathSize) {
	      smallestLeftPathSize = size;
	      smallestLeftPath = &p;
	    }
	    else if (size == smallestLeftPathSize){//for race conditions 
	      if (node < smallestLeftPath->path[0])
		smallestLeftPath = &p;
	    }
	  }
	}
	else {
	  s_iter = satisfiedNodesGo.find(node);
	  
	  if (s_iter!=satisfiedNodesGo.end()) {
	    doNext=false;
	  
	    if (size < smallestRightPathSize) {
	      smallestRightPathSize = size;
	      smallestRightPath = &p;
	    }
	    else if (size == smallestRightPathSize){//for race conditions 
	      if (node < smallestRightPath->path[0])
		smallestRightPath = &p;
	    }
	  }
	}
	
	if (doNext) {
	  
	  if (p.predNotG) {
	    prG.addPath(p);
	    leftLinks++;
	    satisfiedNodesPr.insert(node);
	  }
	  else {	    
	    gsG.addPath(p);
	    rightLinks++;	    
	    satisfiedNodesGo.insert(node);
	  }	  	
	  
	}
      }
      if (leftLinks > 0 && rightLinks==0) {		
	gsG.addPath(*smallestRightPath);
      }
      else if (leftLinks == 0 && rightLinks > 0) {		
	prG.addPath(*smallestLeftPath);
      }      
    }    
  }
};
#endif
/* 
 * File:   bestPathfinder.cpp
 * Author: aris
 * 
 * Created on November 16, 2011, 4:39 PM
 */

#include "bestPathfinder.h"

BestPathfinder::BestPathfinder(Graph& fullG, set<int>& tNodes, set<int>& pNodes,int max) {
    this->max = max;
    this->fullG = &fullG;
    trueNodes=tNodes;
    predictedNodes=pNodes;    
    for (s_iter=trueNodes.begin();s_iter!=trueNodes.end();s_iter++) {
        int node = *s_iter;
        iter = pathsPerNode.find(node);        
        if (iter == pathsPerNode.end()) {            
            vector<Path> paths = fullG.getAncestralPathsOfNode(node,max);            
            pathsPerNode[node] = paths;
	    
	}
    }        
    for (s_iter=predictedNodes.begin();s_iter!=predictedNodes.end();s_iter++) {
        int node = *s_iter;	
        iter = pathsPerNode.find(node);
        if (iter == pathsPerNode.end()) {
            vector<Path> paths = fullG.getAncestralPathsOfNode(node,max);
            pathsPerNode[node] = paths;	    
	    
        }

    }    
}

BestPathfinder::BestPathfinder(const BestPathfinder& orig) {
}

BestPathfinder::~BestPathfinder() {
}

vector<int> BestPathfinder::getBestPairs(int initialNode,GreatPath& gp, int& dist) {    
    set<int>::iterator sf_iter;
    vector<Path>& gpv = gp.paths;
    vector<Path>& nodePaths = pathsPerNode[initialNode];
    int GBLength = 1000000;
    int GBestAncestor;
    int bestOtherNode;    
    vector<int> ret;    
    int curOtherNode;
    for (int i=0;i<nodePaths.size();i++) {//for each path        
        Path& curPath = nodePaths[i];
        vector<int>& cur_v = curPath.path;        
        int node;
        int bestLength = 1000000;        
        int l;	
        for (l=0;l<cur_v.size();l++) {//for each node of the path
            node = cur_v[l];//the ancestor
            if (gp.contains(node)) {                                              
                 for (int j=0;j<gpv.size();j++) {
                      Path& gp_path = gpv[j];                
		      if (gp_path.contains(node)) {			
			int length = gp_path.getLength(node);
			curOtherNode = gp_path.getHead();
			if (length < bestLength) {
			    bestLength = length;                                
			    
			    int fullLength = l + bestLength;		
			    if (fullLength < GBLength) {
				GBLength = fullLength;
				GBestAncestor = node;
				ret.clear();
				ret.push_back(curOtherNode);	    	    
				dist = fullLength;
			    }
			    
			    else if (fullLength == GBLength){	    
				ret.push_back(curOtherNode);				
			    }
			    
			}
			else if (length==bestLength) {
			  curOtherNode = gp_path.getHead();
			  int fullLength = l + bestLength;		
			  if (fullLength < GBLength) {
			      GBLength = fullLength;
			      GBestAncestor = node;
			      ret.clear();
			      ret.push_back(curOtherNode);	    	    
			      dist = fullLength;
			  }
			  
			  else if (fullLength == GBLength){	    
			      ret.push_back(curOtherNode);			      
			  }
			    //bestPath.print();
			}
		      }		   
		}
		break;
	  }                                                       	  
        }                                    
     }            
    return ret;
}
vector<sum_pair> BestPathfinder::getBestPath(int initialNode,GreatPath& gp) {    
    set<int>::iterator sf_iter;
    vector<Path>& gpv = gp.paths;
    vector<Path>& nodePaths = pathsPerNode[initialNode];
    int GBLength = 1000000;
    int GBestAncestor;
    int bestOtherNode;
    Path GBestPathNode;
    //Path GBestPathFromAllPaths;
    vector <sum_pair> ret;    
    int curOtherNode;                                                               
    for (int i=0;i<nodePaths.size();i++) {//for each path        
        Path& curPath = nodePaths[i];
        vector<int>& cur_v = curPath.path;
        //curPath.print();
        int node;
        int bestLength = 1000000;
        Path bestPath;
        int l;	
        for (l=0;l<cur_v.size();l++) {//for each node of the path
	  node = cur_v[l];//the ancestor
	  if (gp.contains(node)) {                                
	    for (int j=0;j<gpv.size();j++) {
	      Path& gp_path = gpv[j];                
	      if (gp_path.contains(node)) {
		int length = gp_path.getLength(node);
		curOtherNode = gp_path.getHead();
		GBestAncestor = node;
		if (length < bestLength) {
		  bestLength = length;		  
		  int fullLength = l + bestLength;		  
		  if (fullLength < GBLength) {
		      GBLength = fullLength;		      
		      ret.clear();
		      bestPath.fixPath(gp_path.path,node);
		      GBestPathNode.fixPath(cur_v, GBestAncestor);
		      pair<Path,Path> newRetPath (GBestPathNode,bestPath);
		      sum_pair sum_p (newRetPath);
		      ret.push_back(sum_p);
		  }
		  else if (fullLength == GBLength){
		    bestPath.fixPath(gp_path.path,node);
		    GBestPathNode.fixPath(cur_v, GBestAncestor);
		    pair<Path,Path> newRetPath (GBestPathNode,bestPath);
		    sum_pair sum_p (newRetPath);
		    ret.push_back(sum_p);
		  }
		}
		else if (length==bestLength) {
		  curOtherNode = gp_path.getHead();
		  int fullLength = l + bestLength;		  
		  if (fullLength < GBLength) {
		    GBLength = fullLength;
		    ret.clear();
		    bestPath.fixPath(gp_path.path,node);
		    GBestPathNode.fixPath(cur_v, GBestAncestor);
		    pair<Path,Path> newRetPath (GBestPathNode,bestPath);
		    sum_pair sum_p (newRetPath);
		    ret.push_back(sum_p);
		  }
		  else if (fullLength == GBLength) {
		    bestPath.fixPath(gp_path.path,node);
		    GBestPathNode.fixPath(cur_v, GBestAncestor);
		    pair<Path,Path> newRetPath (GBestPathNode,bestPath);
		    sum_pair sum_p (newRetPath);
		    ret.push_back(sum_p);  
		  }
		}
	      }
		
	    }
	    break;
	  }                                    
        }
    }
    sort(ret.begin(),ret.end());
    return ret;
}
//This chooses best paths

double BestPathfinder::computeMGIA (int maxError, set<int>& intersection) {
  double ret = 0.0;
  set<int> gsCleared = getSubsetMinusCommon(trueNodes,predictedNodes);    
  set<int> prCleared = getSubsetMinusCommon(predictedNodes,trueNodes);
  
  GreatPath gsGP (trueNodes, pathsPerNode);
  GreatPath prGP (predictedNodes, pathsPerNode);
  MatchCollection mc;
  for (s_iter = intersection.begin();s_iter!=intersection.end();s_iter++) {
    mc.addMatch(*s_iter,*s_iter,0);
  }
  for (s_iter = gsCleared.begin();s_iter!=gsCleared.end();s_iter++) {        
    int GIElength;
    vector<int> possiblePredictedMatches = getBestPairs (*s_iter,prGP,GIElength);    
    if (GIElength >= maxError) 
	ret += maxError;	    	
    else {
	for (int i=0;i<possiblePredictedMatches.size();i++){
	  //cout << "golden addMatch operation" << possiblePredictedMatches[i] << " " << *s_iter << " " << GIElength << endl;
	  mc.addMatch(possiblePredictedMatches[i],*s_iter,GIElength);
	}
    }	
  }       
  for (s_iter = prCleared.begin();s_iter!=prCleared.end();s_iter++) {
    int GIElength;
    vector<int> possibleGoldenMatches = getBestPairs (*s_iter,gsGP,GIElength);         
    if (GIElength >= maxError) 
	ret += maxError;	    	
    else {
	for (int i=0;i<possibleGoldenMatches.size();i++){
	  //cout << "predicted addMatch operation"<< *s_iter << " " << possibleGoldenMatches[i] << " " << GIElength << endl;
	  mc.addMatch(*s_iter,possibleGoldenMatches[i],GIElength);
	}
    }        
  }  
  mc.removeExtraMatches();
  int mc_err = mc.computeError();
  ret+=mc_err;
  return ret;
  
}

void BestPathfinder::createSubGraphs (Graph& gsG, Graph& prG) {
    
    set<int> gsCleared = getSubsetMinusCommon(trueNodes,predictedNodes);    
    set<int> prCleared = getSubsetMinusCommon(predictedNodes,trueNodes);
    set<int> intersection = getIntrOfSets (trueNodes,predictedNodes);    
    AncestorLinks al(trueNodes,predictedNodes,gsG,prG);    
    GreatPath gsGP (trueNodes, pathsPerNode);
    GreatPath prGP (predictedNodes, pathsPerNode);
        
    map<int, map<int, vector <pair<Path,Path> > > > goldenPathsPerGnode;                     
    map<int, map<int, vector <pair<Path,Path> > > > predictedPathsPerPnode;
    map<int, set<int> > probablePathNodesPerImportantNode;
    
    
    for (s_iter = gsCleared.begin();s_iter!=gsCleared.end();s_iter++) {
        vector <sum_pair> twoPaths = getBestPath(*s_iter,prGP);
	int g = *s_iter;
	//goldenPathsPerGnode[g] = twoPaths;
	for (int i=0;i<twoPaths.size();i++) {	  
	  Path& pg = twoPaths[i].p.first;
	  set<int>& nUsedOfIn = probablePathNodesPerImportantNode[g];
	  for (int j=0;j<pg.path.size();j++)
		nUsedOfIn.insert(pg.path[j]);	 	  
	  
	  pg.predNotG = false;
	  int anc = pg.path[pg.path.size()-1];	  	  
	  Path& pp = twoPaths[i].p.second;	  
	  pp.predNotG=true;	  
	  int pre = pp.path[0];
	  al.addAncAndTargets(anc,pre,g);
	  pair<Path,Path> newRetPath (pg,pp);
	  goldenPathsPerGnode[g][pre].push_back(newRetPath);
	}
    }
    for (s_iter = prCleared.begin();s_iter!=prCleared.end();s_iter++) {
        vector <sum_pair> twoPaths = getBestPath(*s_iter,gsGP);
	int pre = *s_iter;	
	for (int i=0;i<twoPaths.size();i++) {	  
	  Path& pp = twoPaths[i].p.first;
	  set<int>& nUsedOfIn = probablePathNodesPerImportantNode[pre];
          for (int j=0;j<pp.path.size();j++)
                nUsedOfIn.insert(pp.path[j]);

	  int anc = pp.path[pp.path.size()-1];	  	  	  
	  pp.predNotG=true;	  	  	  
	  Path& pg = twoPaths[i].p.second;
	  pg.predNotG = false;
	  int g = pg.path[0];	  	  
	  al.addAncAndTargets(anc,pre,g);
	  pair<Path,Path> newRetPath (pp,pg);
	  predictedPathsPerPnode[pre][g].push_back(newRetPath);
	}
    }
    set<int> BestAncs = al.selectBestAncs(intersection,(predictedNodes.size()+trueNodes.size()));
    set<int>::iterator anc_iter;
    map<int, map<int, vector <pair<Path,Path> > > >::iterator p_iter;
    
    for (p_iter = goldenPathsPerGnode.begin();p_iter!=goldenPathsPerGnode.end();p_iter++) {
      int g = p_iter->first;
      map<int, vector <pair<Path,Path> > >& paths = p_iter->second;
      map<int, vector <pair<Path,Path> > >::iterator other_iter;
            
      for (other_iter=paths.begin();other_iter!=paths.end();other_iter++) {
	Path* bestPathPre;
	Path* bestPathGo;
	int bestPre;
	int bestGo;
	int bestPathCommonNodes = -1;
	int bestCommonNodesWithUnusedNodes = 0;
	int bestSumForRaceConditions;
	int anc;
	int pre = other_iter->first;
	vector <pair<Path,Path> > twoPaths = other_iter->second;	
	for (int i=0;i<twoPaths.size();i++) {	  
	  Path& pg = twoPaths[i].first;	 	
	  anc = pg.path[pg.path.size()-1];
	  anc_iter=BestAncs.find(anc);
	  if (anc_iter!=BestAncs.end()) {
	    Path& pp = twoPaths[i].second;
	    int sumForRaceConditions = 0;
	    int cn = al.getCommonNodes(pp,pg,sumForRaceConditions);
	    if (cn > bestPathCommonNodes) {
	      bestPathCommonNodes=cn;
	      bestPathGo=&pg;
	      bestPathPre=&pp;
	      bestPre=pre;
	      bestGo=g;
	      bestSumForRaceConditions = sumForRaceConditions;
	      bestCommonNodesWithUnusedNodes=0;
	    }
	    else if (cn==bestPathCommonNodes) {
	      int bestCommonG = 0;
	      int bestCommonP = 0;
	      for (set<int>::iterator iterTemp=gsCleared.begin();iterTemp!=gsCleared.end();iterTemp++) {
		int local_g = *iterTemp;
		if (local_g!=g){
			map<int,set<int> >::iterator ppn = probablePathNodesPerImportantNode.find(local_g);
			if (ppn!=probablePathNodesPerImportantNode.end()) {
			  int cnG=al.getCommonNodes(pg,ppn->second);			
			  if (cnG > bestCommonG)
				bestCommonG=cnG;		
			}
		}
	      }
	      for (set<int>::iterator iterTemp=prCleared.begin();iterTemp!=prCleared.end();iterTemp++) {
		int local_p = *iterTemp;
		if (local_p!=pre){
			map<int,set<int> >::iterator ppn = probablePathNodesPerImportantNode.find(local_p);
			if (ppn!=probablePathNodesPerImportantNode.end()) {
			  int cnP=al.getCommonNodes(pp,ppn->second);			
			  if (cnP > bestCommonP)
				  bestCommonP=cnP;		
			}
		}
	      }
	      int bestCommon = bestCommonG+bestCommonP;
	      if (bestCommon > bestCommonNodesWithUnusedNodes) {
		bestPathGo=&pg;
		bestPathPre=&pp;
		bestPre=pre;
		bestGo=g;
		bestSumForRaceConditions = sumForRaceConditions;
		bestCommonNodesWithUnusedNodes=bestCommon;
	      }
	      else if (sumForRaceConditions < bestSumForRaceConditions) {		
		bestPathGo=&pg;
		bestPathPre=&pp;
		bestPre=pre;
		bestGo=g;
		bestSumForRaceConditions = sumForRaceConditions;
	      }	      
	    }
	  }
	}
	if (bestPathCommonNodes!=-1)
	  al.addPath(anc,bestPre,*bestPathPre,bestGo,*bestPathGo);
      }                		         
    }
    for (p_iter = predictedPathsPerPnode.begin();p_iter!=predictedPathsPerPnode.end();p_iter++) {
      int pre = p_iter->first;
      map<int, vector <pair<Path,Path> > >& paths = p_iter->second;
      map<int, vector <pair<Path,Path> > >::iterator other_iter;
      for (other_iter=paths.begin();other_iter!=paths.end();other_iter++) {
	Path* bestPathPre;
	Path* bestPathGo;
	int bestPre;
	int bestGo;
	int bestPathCommonNodes = -1;
	int bestCommonNodesWithUnusedNodes = 0;
	int bestSumForRaceConditions;
	int anc;
	int g = other_iter->first;
	vector <pair<Path,Path> > twoPaths = other_iter->second;	
	for (int i=0;i<twoPaths.size();i++) {	  
	  Path& pp = twoPaths[i].first;	 	
	  anc = pp.path[pp.path.size()-1];
	  anc_iter=BestAncs.find(anc);
	  if (anc_iter!=BestAncs.end()) {
	    Path& pg = twoPaths[i].second;
	    int sumForRaceConditions = 0;
	    int cn = al.getCommonNodes(pp,pg,sumForRaceConditions);
	    if (cn > bestPathCommonNodes) {
	      bestPathCommonNodes=cn;
	      bestPathGo=&pg;
	      bestPathPre=&pp;
	      bestPre=pre;
	      bestGo=g;
	      bestSumForRaceConditions = sumForRaceConditions;
	      bestCommonNodesWithUnusedNodes=0;
	    }
	    else if (cn==bestPathCommonNodes) {
	      int bestCommonG = 0;
	      int bestCommonP = 0;
	      for (set<int>::iterator iterTemp=gsCleared.begin();iterTemp!=gsCleared.end();iterTemp++) {
		int local_g = *iterTemp;
		if (local_g!=g){
			map<int,set<int> >::iterator ppn = probablePathNodesPerImportantNode.find(local_g);
			if (ppn!=probablePathNodesPerImportantNode.end()) {
			  int cnG=al.getCommonNodes(pg,ppn->second);			
			  if (cnG > bestCommonG)
				bestCommonG=cnG;		
			}
		}
	      }
	      for (set<int>::iterator iterTemp=prCleared.begin();iterTemp!=prCleared.end();iterTemp++) {
		int local_p = *iterTemp;
		if (local_p!=pre){
			map<int,set<int> >::iterator ppn = probablePathNodesPerImportantNode.find(local_p);
			if (ppn!=probablePathNodesPerImportantNode.end()) {
			  int cnP=al.getCommonNodes(pp,ppn->second);			
			  if (cnP > bestCommonP)
				  bestCommonP=cnP;		
			}
		}
	      }	      
	      int bestCommon = bestCommonG+bestCommonP;
	      if (bestCommon > bestCommonNodesWithUnusedNodes) {
		bestPathGo=&pg;
		bestPathPre=&pp;
		bestPre=pre;
		bestGo=g;
		bestSumForRaceConditions = sumForRaceConditions;
		bestCommonNodesWithUnusedNodes=bestCommon;
	      }
	      else if (sumForRaceConditions < bestSumForRaceConditions) {		
		bestPathGo=&pg;
		bestPathPre=&pp;
		bestPre=pre;
		bestGo=g;
		bestSumForRaceConditions = sumForRaceConditions;
	      }	      
	    }
	  }
	}
	if (bestPathCommonNodes!=-1)
	  al.addPath(anc,bestPre,*bestPathPre,bestGo,*bestPathGo);
      }                		         
    }              
    al.selectBestPaths (gsG,prG,intersection,(predictedNodes.size()+trueNodes.size()),BestAncs);
}

void BestPathfinder::printPathsPerNode() {
    for (iter=pathsPerNode.begin();iter!=pathsPerNode.end();iter++) {
        cout << "Node = " << iter->first << endl;
        for (int i=0;i<iter->second.size();i++) {
            iter->second[i].print();
        }
        cout << endl;
    }
}

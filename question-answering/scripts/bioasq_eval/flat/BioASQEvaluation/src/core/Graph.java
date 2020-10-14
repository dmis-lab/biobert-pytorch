/*
 * Copyright 2013,2014 BioASQ project: FP7/2007-2013, ICT-2011.4.4(d), 
 *  Intelligent Information Management, 
 *  Targeted Competition Framework grant agreement n° 318652.
 *  www: http://www.bioasq.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 * @author Ioannis Partalas
 */
package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Graph {
    Map parent_child; //parent child relations, for each parent we keep an ArrayList of children
    Map child_parent;
    
    Set graph_parents;
    Set graph_children;
    
    public Graph()
    {
        parent_child = new TreeMap();
        child_parent = new TreeMap();
        
        graph_children = new TreeSet<Integer>();
        graph_parents = new TreeSet<Integer>();
    }
    
    public void loadGraphFromFile(String filename){
        BufferedReader br = null;
        int row=0;
        int fathers=0;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            while((line=br.readLine())!=null){
                row++;
                String nodes[] = line.split("\\s+");
                if(nodes.length!=2)
                {
                    System.out.println("Error with parsing. Please check file.\n"
                            + "Line: "+row);
                    System.exit(0);
                }
                int father  = Integer.parseInt(nodes[0]);
                int child = Integer.parseInt(nodes[1]);
                
                graph_children.add(new Integer(child));
                graph_parents.add(new Integer(father));
                
                if(!parent_child.containsKey(father)){
                    ArrayList<Integer> children= new ArrayList<Integer>();
                    children.add(child);
                    parent_child.put(new Integer(father), children);
                    fathers++;
                } 
                else{
                    ArrayList<Integer> get = (ArrayList<Integer>)parent_child.get(father);
                    get.add(child);
                }
                
                if(!child_parent.containsKey(child)){
                    ArrayList<Integer> myparents = new ArrayList<Integer>();
                    myparents.add(new Integer(father));
                    child_parent.put(new Integer(child), myparents);
                    
                }
                else
                {
                    ArrayList<Integer> myparents = (ArrayList<Integer>)child_parent.get(child);
                    myparents.add(new Integer(father));
                }
                
                
            }
            
            
            
        } catch (IOException ex) {
            
            System.out.println("File not found: "+filename + " or unable to read file");
            System.out.println(ex.getMessage());
        }finally{
            try{
                if (br!=null){
                    br.close();
                }
          
            }catch(IOException ex){
                System.out.println(ex);
            }
        }
        
        System.out.println("Loaded fathers: "+fathers);
        //System.out.println("Total num of classes: "+num_of_nodes);
    }
       
    public boolean isLeaf(Integer node)
    {
        if(parent_child.containsKey(node))
           return true;

        return false;
    }
       
    public void printGraphStats()
    {
           System.out.println("Graph parents: "+ graph_parents.size());
           System.out.println("Graph childern: "+ graph_children.size());
           
           graph_children.removeAll(graph_parents);
           System.out.println("Leaves: "+ graph_children.size());
           
           //graph_parents.removeAll(graph_children);
           //System.out.println("First level: "+ graph_parents.size());
    }

    private ArrayList<Integer> getParentOf(Integer child) {
        return (ArrayList<Integer>)child_parent.get(child);
    }
    
    public static void main(String[] args){
        
        Graph graph = new Graph();
        graph.loadGraphFromFile(args[0]);
        graph.printGraphStats();
        //graph.findFirstLevelOfHierarchy();
    }
}

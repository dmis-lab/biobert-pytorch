/*
 * Copyright 2014 BioASQ project: FP7/2007-2013, ICT-2011.4.4(d), 
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
package converters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
/** This script called to prepare files for BioASQ Task A evaluation, for flat measures.
 * 
 *  Example call
 *      java -cp BioASQEvaluation2018.jar converters.MapMeshResults "...\mesh_year_INT.txt" "...\labels_to_map.txt" "...\labels_mapped.txt" 
 *              *labels_mapped.txt is the output file name
 * 
 * @author tasosnent
 */
public class MapMeshResults {
    
    Map mapping;
    
    public MapMeshResults()
    {
        mapping  = new TreeMap();
    }
    
    public void loadMapping(String mapfile)
    {
        BufferedReader br = null;
        
        try {
                br = new BufferedReader(new FileReader(mapfile));
                String line;
                while((line=br.readLine())!=null){
                    String nodes[] = line.split("\\s+");
                    mapping.put(nodes[0],Integer.parseInt(nodes[1]));
            }
                
         br.close();
        }catch(IOException ex){
        
        }
    }
    
    public void mapMeshResults(String source,String target)
    {
        BufferedReader br = null;
        BufferedWriter bw = null;
        
        try {
                br = new BufferedReader(new FileReader(source));
                bw = new BufferedWriter(new FileWriter(target));
                
                String line;
                while((line=br.readLine())!=null){
                    String labels[] = line.split("\\s+");
                    for(int i=0;i<labels.length;i++){
                        Integer lab = (Integer)mapping.get(labels[i]);
                        if(lab!=null)
                            bw.write(lab.intValue()+" ");
                    }
                    bw.write("\n");
            }
                
         br.close();
         bw.close();
        }catch(IOException ex){
        
        }
       
    }
    
      public static void main(String[] args){
          MapMeshResults mapres = new MapMeshResults();
          mapres.loadMapping(args[0]);
          mapres.mapMeshResults(args[1], args[2]);
      }
}

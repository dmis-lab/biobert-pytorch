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
package converters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class OBOToHier {
    
    Map parent_child;
    Map parent_child_int;
    Map mapping;
    Map id_name_map;
    
    public OBOToHier()
    {
        parent_child = new TreeMap();
        parent_child_int = new TreeMap();
        mapping = new TreeMap();
        id_name_map = new TreeMap();
    }
    
    
    public void convertOboToHier(String source)
    {
        int row=0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(source));
            
            String line="";
            int class_ids=1;
            while((line=bf.readLine())!=null)
            {
                row++;
                if(line.startsWith("[Term]"))
                {
                    line = bf.readLine();
                    row++;
                    String ids[] = line.split("\\s+");
                    String id = ids[1];
                   
                    
                    if(mapping.get(id)==null)
                    {
                        mapping.put(id, new Integer(class_ids));
                        class_ids++;
                    }
                    
                    line = bf.readLine(); // here we get the name
                    row++;
                    
                    String name = line.substring(line.indexOf(' ')+1);
                    
                    id_name_map.put(name,id);
                    
                    while(!line.startsWith("is_a"))
                    {
                        line = bf.readLine(); row++;
                    }
                    
                    ArrayList<String> children = new ArrayList<String>();
                    ArrayList<Integer> children_int = new ArrayList<Integer>();
                    
                    while(line!=null&&line.startsWith("is_a")){
                          String tokens[] = line.split("\\s+");
                          String parent = tokens[1];
                          
                     if(mapping.get(parent)==null)
                     {
                        mapping.put(parent, new Integer(class_ids));
                        class_ids++;
                     }
                          
                          ArrayList<String>ch  = (ArrayList<String>)parent_child.get(parent);
                          ArrayList<Integer>ch_int  = (ArrayList<Integer>)parent_child_int.get((Integer)mapping.get(parent));

                          if(ch==null)
                          {
                              children.add(id);
                              parent_child.put(parent,children);
                              children_int.add((Integer)mapping.get(id));
                              parent_child_int.put((Integer)mapping.get(parent),children_int);
                          }
                          else
                          {
                              ch.add(id);
                              ch_int.add((Integer)mapping.get(id));
                          }
                          
                          line = bf.readLine();
                          row++;
                    }
                    
                }
            }
            
            
        } catch (IOException ex) {
            
        }catch(NullPointerException npe){System.out.println(row);}
    }
    
    public void exportToFile(String hier)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(hier));
            
            Iterator iter = parent_child.keySet().iterator();
            while(iter.hasNext())
            {
                String parent = (String)iter.next();
                ArrayList<String> children = (ArrayList<String>)parent_child.get(parent);
                for(int k=0;k<children.size();k++)
                {
                    bw.write(parent+" "+children.get(k)+"\n");
                }
            }
            
            bw.close();
        } catch (IOException ex) {
            
        }
    }
 
    public void exportMapping(String mapp)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapp));
            
            Iterator iter =mapping.keySet().iterator();
            while(iter.hasNext())
            {
                String parent = (String)iter.next();
                Integer int_id = (Integer)mapping.get(parent);
                
                bw.write(parent+" "+int_id.intValue()+"\n");
                
            }
            
            bw.close();
        } catch (IOException ex) {
            
        }
    }
    
      public void exportNameIdMapping(String mapp)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapp));
            
            Iterator iter =id_name_map.keySet().iterator();
            while(iter.hasNext())
            {
                String name = (String)iter.next();
                String id = (String)id_name_map.get(name);
                
                bw.write(name+"="+id+"\n");
                
            }
            
            bw.close();
        } catch (IOException ex) {
            
        }
    }
    
    
    public void exportToFileInt(String hier)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(hier));
            
            Iterator iter = parent_child_int.keySet().iterator();
            while(iter.hasNext())
            {
                Integer parent = (Integer)iter.next();
                ArrayList<Integer> children = (ArrayList<Integer>)parent_child_int.get(parent);
                for(int k=0;k<children.size();k++)
                {
                    bw.write(parent.intValue()+" "+children.get(k).intValue()+"\n");
                }
            }
            
            bw.close();
        } catch (IOException ex) {
            
        }
    }
    
    
    public static void main(String args[])
    {
        OBOToHier bob2hier = new OBOToHier();
        
        bob2hier.convertOboToHier(args[0]);
        bob2hier.exportToFile(args[1]);
        bob2hier.exportToFileInt(args[2]);
        bob2hier.exportMapping(args[3]);
        bob2hier.exportNameIdMapping(args[4]);
    }
}

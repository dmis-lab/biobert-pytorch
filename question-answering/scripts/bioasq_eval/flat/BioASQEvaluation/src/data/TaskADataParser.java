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
package data;

import com.google.gson.stream.JsonReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskADataParser {
    
    HashSet journalList;
    int numeOfArticles=0;
    double labelsPerArticle=0.0;
    HashSet labelsList;
    double labelDensity=0;
    HashSet pmids;
    
    /**
     * 
     * Return a json reader and opens the array
     * 
     */
    public static JsonReader streamParser(String jsonFile) throws IOException {
        
        int count = 0;
        int abstract_count=0;
        int duplicates = 0;
         JsonReader reader =null;
        try {
            reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
            reader.setLenient(true);
            reader.beginObject();
            String nam = reader.nextName();
            System.out.println(nam);
            reader.beginArray();
            
        } catch (Exception ex) {
           System.out.println("File not found");
           System.out.println(ex.toString());
        }
        return reader;
    }
 
    public static void closeReader(JsonReader reader)
    {
        try {
            
            reader.endArray();
            reader.endObject();
        } catch (IOException ex) {
            Logger.getLogger(TaskADataParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static PubMedDocument getNextDocument(JsonReader reader)
    {
        String text=null;
        String title=null;
        String pmid=null;
        String journal=null;
        String[] meshMajor=null;
        
        try {
            if (reader.hasNext()) {
                reader.beginObject();
                
                   while (reader.hasNext()) {
                    String name = reader.nextName();
                    
                    if (name.equals("abstractText")) {
                        
                        text = reader.nextString();
                        
                    } else if (name.equals("journal")) {
                        journal = reader.nextString();
                        
                    } else if (name.equals("meshMajor")) {
                        meshMajor = readLabelsArray(reader);
                        
                    } else if (name.equals("pmid")) {
                        pmid = reader.nextString();
                    } else if (name.equals("title")){
                        title = reader.nextString();
                    }
                    else if (name.equals("year")){
                        reader.skipValue();
                    }
                    else{
                        System.out.println(name);
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
        } catch (Exception ex) {  }

        return new PubMedDocument(text, title, pmid, journal, meshMajor);
    }
    
    public static String[] readLabelsArray(JsonReader reader){
     
    String labels[];
    ArrayList<String> lab = new ArrayList<String>();
    try{
        reader.beginArray();
        while (reader.hasNext()) {
            String nextString = reader.nextString();
            lab.add(nextString);
            }
        reader.endArray();
     }catch(IOException ex){}
        labels = new String[lab.size()];
        labels = lab.toArray(labels);
     return labels;
   }
     
}

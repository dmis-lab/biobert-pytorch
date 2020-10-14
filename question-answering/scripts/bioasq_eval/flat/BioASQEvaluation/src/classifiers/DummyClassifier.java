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
package classifiers;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DummyClassifier {
    
    
    
    public DummyClassifier()
    {
        
    }
    
    
    public void parseStreamAndClassify(String jsonFile,String resultsFile) throws IOException {
        
        String journalName;
        int count = 0;
        int abstract_count=0;
        
        
        
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(resultsFile), "UTF-8"));
            writer.setIndent("    ");
            
            //reader.setLenient(true);
            reader.beginArray();
            writer.beginArray();
            while (reader.hasNext()) {
                
                reader.beginObject();
                writer.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    
                    if (name.equals("abstract")) {
                        abstract_count++;
                        reader.skipValue();
                        
                    } else if (name.equals("pmid")) {
                        String pmid = reader.nextString();
                        writer.name("labels");
                        writeLabels(writer);
                        writer.name("pmid").value(pmid);
                    } else if (name.equals("title")){
                        reader.skipValue();
                    }
                    else{
                        System.out.println(name);
                        reader.skipValue();
                    }
                }
                reader.endObject();
                writer.endObject();
            }
            reader.endArray();
            writer.endArray();
            
            System.out.println("Abstracts: "+abstract_count);
           
            writer.close();
        } catch (FileNotFoundException ex) {
         
        }
    }
    
    public void writeLabels(JsonWriter writer) throws IOException {
        writer.beginArray();
        for(int i=0;i<15;i++){
        writer.value("D005124");
        }
        writer.endArray();
    }
    
    public static void main(String args[])
    {
        DummyClassifier dc = new DummyClassifier();
        try {
            dc.parseStreamAndClassify(args[0], args[1]);
        } catch (IOException ex) {
            
        }
    }
    
}

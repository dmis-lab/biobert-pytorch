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
package tools;

import com.google.gson.stream.JsonReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawStatisticsForPubMedData {
    
    HashSet journalList;
    int numeOfArticles=0;
    double labelsPerArticle=0.0;
    HashSet labelsList;
    double labelDensity=0;
    HashSet pmids;
    
    
    public DrawStatisticsForPubMedData()
    {
        journalList = new HashSet();
        labelsList = new HashSet();
        pmids = new HashSet();
    }
    
    public void parseStream(String jsonFile,String listOfJournals) throws IOException {
        
        String journalName;
        int count = 0;
        int abstract_count=0;
        int duplicates = 0;
        
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
            reader.setLenient(true);
             
             reader.beginObject();
             reader.skipValue();
               
            //System.out.println(nam);
            reader.beginArray();
            while (reader.hasNext()) {
                
                reader.beginObject();
                this.numeOfArticles++;
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    
                    if (name.equals("abstractText")) {
                        abstract_count++;
                        reader.skipValue();
                        
                    } else if (name.equals("journal")) {
                        journalName = reader.nextString();
                        journalList.add(journalName);
                    } else if (name.equals("meshMajor")) {
                        int num_labels = readLabelsArray(reader);
                        count+=num_labels;
                        labelDensity += (double)num_labels/26563.0;
                    } else if (name.equals("pmid")) {
                        int pmid = reader.nextInt();
                        if(!pmids.contains(pmid))
                            pmids.add(pmid);
                        else
                            duplicates++;
                    } else if (name.equals("title")){
                        reader.skipValue();
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
            reader.endArray();
            
            System.out.println("Abstracts: "+abstract_count);
            System.out.println("Duplicates: "+duplicates);

            labelsPerArticle = (double)count/(double)numeOfArticles;
            labelDensity = labelDensity/(double)numeOfArticles;
            exportListOfJournals(listOfJournals);
            printStatistics();
            
        } catch (Exception ex) {
            System.out.println("Abstracts: "+abstract_count);
            System.out.println("Duplicates: "+duplicates);

            labelsPerArticle = (double)count/(double)numeOfArticles;
            labelDensity = labelDensity/(double)numeOfArticles;
            exportListOfJournals(listOfJournals);
            printStatistics();
            Logger.getLogger(DrawStatisticsForPubMedData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public int readLabelsArray(JsonReader reader){
     
        int count=0;
      try{
     reader.beginArray();
     while (reader.hasNext()) {
      String nextString = reader.nextString();
      labelsList.add(nextString);
       count++;
     }
     reader.endArray();
     }catch(IOException ex){}
        
     return count;
   }

    private void exportListOfJournals(String listOfJournals) {
        BufferedWriter bw=null;
        try {
            bw = new BufferedWriter(new FileWriter(listOfJournals));
            
            Iterator iter = journalList.iterator();
            
            while(iter.hasNext())
            {
                String jour = (String)iter.next();
                bw.write(jour+"\n");
            }
            
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(DrawStatisticsForPubMedData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printStatistics() {
        System.out.println("Number of articles: "+numeOfArticles);
        System.out.println("Labels per article: "+labelsPerArticle);
        System.out.println("Number of labels: "+labelsList.size());
        System.out.println("Density: "+labelDensity);

    }
    
    public static void main(String args[])
    {
       DrawStatisticsForPubMedData ds = new DrawStatisticsForPubMedData();
        try {
            ds.parseStream(args[0], args[1]);
        } catch (IOException ex) {
            Logger.getLogger(DrawStatisticsForPubMedData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
}

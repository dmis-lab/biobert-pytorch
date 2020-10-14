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
package data.PreProcess;

import com.google.gson.stream.JsonReader;
import data.PubMedDocument;
import data.TaskADataParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tartarus.snowball.SnowballStemmer;

/**
 *
 * @author alvertos
 */
public class ExtractVocabulary {
    
    HashSet vocabulary;
    TreeMap uniqueWords;
    JsonReader reader;

    public ExtractVocabulary()
    {
        
    }
    
    public ExtractVocabulary(String datafile) {
        vocabulary = new HashSet();
        uniqueWords = new TreeMap();
        try {
            reader = TaskADataParser.streamParser(datafile);
            
        } catch (IOException ex) {
            
        }
    }
    
    public void makeVoc(String vocfile,String uniquefile) throws InstantiationException, IllegalAccessException
    {
        int numofdocs=0;
        Class stemClass;
        SnowballStemmer stemmer=null;
        try {
            stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
            stemmer = (SnowballStemmer)stemClass.newInstance();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try {
                    while(reader.hasNext()){
                    PubMedDocument nextDocument = TaskADataParser.getNextDocument(reader);
                    numofdocs++;
                    if(numofdocs%100000==0){
                        System.out.println(numofdocs + " of documents have been processed");
                        System.out.println("Size of vocabulary: "+vocabulary.size());
                    }
                        
                    String absrt = nextDocument.getText();
                    String words[] = absrt.split("[\\W]+");
                    //String wordsInLowerCase[] = new String[words.length]; 
                    String wordInLowerCase;

                    
                    for (int k = 0; k < words.length; k++)
                    {
                        wordInLowerCase = words[k].toLowerCase();
                        stemmer.setCurrent(wordInLowerCase);
                        
	      if (stemmer.stem()) {
                           wordInLowerCase = stemmer.getCurrent();
	     }
                
                        if(uniqueWords.containsKey(wordInLowerCase))
                        {Integer freq = (Integer)uniqueWords.get(wordInLowerCase);
                        uniqueWords.put(wordInLowerCase,freq.intValue()+1);}
                        else{uniqueWords.put(wordInLowerCase,1);}
                        vocabulary.add(wordInLowerCase);
              
                    }        
            
            }
                    
             writeVocabularyToFile(vocfile);
             writeUniqueWordsToFile(uniquefile);
                    
        } catch (Exception ex) {
             writeVocabularyToFile(vocfile);
             writeUniqueWordsToFile(uniquefile);
        }
        
        TaskADataParser.closeReader(reader);
        
    }

    /**
     * @param vocfile The vocabulary that will be used to vectorize the documents. For each document a term frequency will be
     *                   calculated
     * @param namepmidf
     * @param pmidintegerf
     * @param outfile
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public void vectorizeDocuments(String vocfile,String namepmidf,String pmidintegerf,String outfile) throws InstantiationException, IllegalAccessException
    {
        int numofdocs=0;
        Class stemClass;
        SnowballStemmer stemmer=null;
        try {
            stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
            stemmer = (SnowballStemmer)stemClass.newInstance();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        TreeMap vocab = loadVocabularyMap(vocfile);
        TreeMap namepmid = loadNamePMIDMapping(namepmidf);
        TreeMap pmidinteger = loadPMIDIntegerMapping(pmidintegerf);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outfile));
            while(reader.hasNext()){
                TreeMap doc = new TreeMap();
                PubMedDocument nextDocument = TaskADataParser.getNextDocument(reader);
                numofdocs++;
                if(numofdocs%10000==0){
                    System.out.println(numofdocs + " of documents have been processed");
                }

                String absrt = nextDocument.getText();
                String words[] = absrt.split("[\\W]+");
                //String wordsInLowerCase[] = new String[words.length]; 
                String wordInLowerCase;

                String[] meshMajor = nextDocument.getMeshMajor();

                for (int k = 0; k < words.length; k++)
                {
                    wordInLowerCase = words[k].toLowerCase();
                    stemmer.setCurrent(wordInLowerCase);

                    if (stemmer.stem()) {
                                 wordInLowerCase = stemmer.getCurrent();
                      }

                    if(vocab.containsKey(wordInLowerCase))
                    {
                        if(doc.containsKey(wordInLowerCase))
                        {
                            Integer freq = (Integer)doc.get(wordInLowerCase);
                            doc.put(wordInLowerCase, freq.intValue()+1);
                        }else{doc.put(wordInLowerCase, 1);}
                    }

                }        

               // System.out.println("Size of vectorized doc:"+doc.size());
                String vector = vectorToString(doc, vocab);
                for(int i=0;i<meshMajor.length;i++)
                {
                    String pmid = (String)namepmid.get(meshMajor[i]);
                    Integer intid = (Integer)pmidinteger.get(pmid);
                    if(i==0)
                    bw.write(intid.toString());
                    else
                        bw.write(","+intid.toString());
                }
                bw.write(vector+"\n");
            }
                    
             bw.close();
             
        } catch (Exception ex) {
            try {
                bw.close();
            } catch (IOException ex1) {
                Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        TaskADataParser.closeReader(reader);
        
    }

    String vectorToString(TreeMap document,TreeMap vocab)
     {
         String vec = "";
         Iterator iter = document.keySet().iterator();
         while(iter.hasNext())
         {
             String word = (String)iter.next();
             Integer id = (Integer)vocab.get(word);
             
             Integer freq = (Integer)document.get(word);
             vec+=" "+id.intValue()+":"+freq.doubleValue();
         }
         return vec;
     }
    
    public TreeMap loadPMIDIntegerMapping(String mapfile)
    {
        BufferedReader br = null;
        TreeMap mapping = new TreeMap();
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
        
        return mapping;
    }

    public TreeMap loadNamePMIDMapping(String mapfile)
    {
        BufferedReader br = null;
        TreeMap mapping = new TreeMap();
        try {
                br = new BufferedReader(new FileReader(mapfile));
                String line;
                while((line=br.readLine())!=null){
                    String nodes[] = line.split("=");
                    mapping.put(nodes[0],nodes[1]);
            }
                
         br.close();
        }catch(IOException ex){
        
        }
        
        return mapping;
    }

    private void writeVocabularyToFile(String vocfile) {
        BufferedWriter bw = null;
        try {
            bw = new  BufferedWriter(new FileWriter(vocfile));
            
            Iterator iter =  this.vocabulary.iterator();
            while(iter.hasNext())
            {
                String word = (String)iter.next();
                bw.write(word+"\n");
            }
            
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeUniqueWordsToFile(String uniqueWordsfile) {
        BufferedWriter bw = null;
        try {
            bw = new  BufferedWriter(new FileWriter(uniqueWordsfile));
            
            Iterator iter =  this.uniqueWords.keySet().iterator();
            while(iter.hasNext())
            {
                String word = (String)iter.next();
                Integer freq = (Integer)uniqueWords.get(word);
                bw.write(word+" "+freq.intValue()+"\n");
            }
            
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removeStopWords(String filestopwords,String vocabularyfile,String outfile){
        HashSet stopwords = loadStopWords(filestopwords);
        vocabulary = loadVocabulary(vocabularyfile);
        HashSet voc_new = new HashSet();
        System.out.println("Size of vocabular: "+vocabulary.size());
        Iterator iter = vocabulary.iterator();
        
        while(iter.hasNext())
        {
            String word = (String)iter.next();
            if(!stopwords.contains(word))
                voc_new.add(word);
        }
        System.out.println("Size of vocabular after stopword removal: "+voc_new.size());
        writeVocabularyToFile(voc_new,outfile);
        
    }

    private void removeLowFrequencyWords(String filesfreqs,String vocabularyfile,String outfile,int min_freq){
        TreeMap freqsfile = loadDocTermFrequencies(filesfreqs);
        vocabulary = loadVocabulary(vocabularyfile);
        HashSet voc_new = new HashSet();
        System.out.println("Size of vocabular: "+vocabulary.size());
        Iterator iter = vocabulary.iterator();
        
        while(iter.hasNext())
        {
            String word = (String)iter.next();
            Integer freq = (Integer)freqsfile.get(word);
            if(freq.intValue()>min_freq)
                voc_new.add(word);
        }
        System.out.println("Size of vocabular after low frequency words removal: "+voc_new.size());
        writeVocabularyToFile(voc_new,outfile);
        
    }

    private HashSet loadStopWords(String filestopwords) {
         HashSet list =new  HashSet();
        try {
            
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(filestopwords));
            String line=null;
           while((line = br.readLine())!=null)
           {
               list.add(line);
           }
        } catch (IOException ex) {
            
        }
        return list;
    }
    
    private HashSet loadVocabulary(String filevoc) {
        HashSet list =new  HashSet();
        try {
            
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(filevoc));
            String line=null;
           while((line = br.readLine())!=null)
           {
               list.add(line);
           }
        } catch (IOException ex) {
            
        }
        return list;
    }
          
    private TreeMap loadVocabularyMap(String filevoc) {
         TreeMap list =new  TreeMap();
         int counter=1;
        try {
            
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(filevoc));
            String line=null;
           while((line = br.readLine())!=null)
           {
               list.put(line,counter++);
           }
        } catch (IOException ex) {
            
        }
        return list;
    }
  
    private TreeMap loadDocTermFrequencies(String filefreq){
       TreeMap list =new  TreeMap();
         
        try {
            
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(filefreq));
            String line=null;
           while((line = br.readLine())!=null)
           {
               String temp[] = line.split("\\s+");
               list.put(temp[0],Integer.parseInt(temp[1]));
           }
        } catch (IOException ex) {
            
        }
        return list;
    }
  
    public static void main(String args[])
    {
        if(args[0].equals("-makeVoc"))
        {
        ExtractVocabulary evoc = new ExtractVocabulary(args[1]);
        try {
            evoc.makeVoc(args[2],args[3]);
        } catch (InstantiationException ex) {
            
        } catch (IllegalAccessException ex) {
            
        }
        }
        
        if(args[0].equals("-stopwords"))
        {
            ExtractVocabulary evoc = new ExtractVocabulary();
            evoc.removeStopWords(args[1], args[2], args[3]);
        }
        
        if(args[0].equals("-lowfreq"))
        {
            ExtractVocabulary evoc = new ExtractVocabulary();
            evoc.removeLowFrequencyWords(args[1], args[2], args[3],Integer.parseInt(args[5]));
        }
        
        if(args[0].equals("-vectorize"))
        {
            ExtractVocabulary evoc = new ExtractVocabulary(args[1]);
            try {
                evoc.vectorizeDocuments(args[2],args[3],args[4],args[5]);
            } catch (InstantiationException ex) {
                
            } catch (IllegalAccessException ex) {
            }
        }
    }

    private void writeVocabularyToFile(HashSet voc_new, String outfile) {
        BufferedWriter bw = null;
        try {
            bw = new  BufferedWriter(new FileWriter(outfile));
            
            Iterator iter =  voc_new.iterator();
            while(iter.hasNext())
            {
                String word = (String)iter.next();
                bw.write(word+"\n");
            }
            
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ExtractVocabulary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

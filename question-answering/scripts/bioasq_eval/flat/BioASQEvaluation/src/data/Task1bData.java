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
import com.google.gson.stream.JsonToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Task1bData {
   
    ArrayList<Question> questions;
    int VERSION_OF_CHALLENGE; 
    boolean isGold;
    
    /**
     * Data Loader for gold files and submissions
     * 
     * @param version   VERSION_OF_CHALLENGE, Use version 2 for BioASQ1&2, version 3 for BioASQ3&4, version 5 since BioASQ5, version 8 since BioASQ8
     * @param isGold    Whether data to read are for gold data or not (since BioASQ5 different format for gold and submitted data, i.e. synonyms only in gold data)
     */
    public Task1bData(int version, boolean isGold)
    {
        questions = new ArrayList<Question>();
        VERSION_OF_CHALLENGE = version;
        this.isGold = isGold;
    }
    
    public void readData(String jsonFile) throws IOException {
        
        int num_questions=0;
        int num_triples=0;
        int type_yesno=0;
        int type_factoid=0;
        int type_list=0;
        int type_summary=0;
        
         try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
           //reader.setLenient(true);
        //    JsonToken peeknext = reader.peek();
        //    peeknext.
            
                reader.beginObject();
                while(reader.hasNext())
                {
                    
                
            String nextfield = reader.nextName();
            if(nextfield.equals("questions"))
            {
            reader.beginArray();
            while (reader.hasNext()) {
                   
                  reader.beginObject();
                  num_questions++;
                  
                  Question qst = new Question();
                  
                  while(reader.hasNext())
                  {
                      String name = reader.nextName();
                      int k=0;
                      if(name.equals("body"))
                      {
                          String body = reader.nextString();
                          qst.setBody(body);
                      }
                      else if(name.equals("triples"))
                      {
                          num_triples++;
                          ArrayList<Triple> triples = readTriplesArray(reader);
                          qst.addTriples(triples);
                      }
                      else if(name.equals("type"))
                      {
                          String type = reader.nextString();
                          
                          if(type.equals("yesno"))
                          {
                              qst.setType(Question.YESNO);
                              type_yesno++;
                              
                          }
                          else if(type.equals("factoid"))
                          {
                              qst.setType(Question.FACTOID);
                              type_factoid++;
                              
                          }
                          if(type.equals("summary"))
                          {
                              qst.setType(Question.SUMMARY);
                              type_summary++;
                              
                          }
                          if(type.equals("list"))
                          {
                              qst.setType(Question.LIST);
                              type_list++;
                              
                          }
                      }
                      else if(name.equals("id"))
                      {
                          String id = reader.nextString();
                          qst.setId(id);
                      }
                      else if(name.equals("concepts"))
                      {
                          ArrayList<String> concepts = readConcepts(reader);
                          qst.addConcepts(concepts);
                      }
                      else if(name.equals("documents"))
                      {
                          ArrayList<String> docs = readDocuments(reader);
                          
                          qst.addDocuments(docs);
                      }
                      else if(name.equals("exact_answer"))
                      {
                          ExactAnswer ea = new ExactAnswer();
                          JsonToken peek = reader.peek();
                          if(peek == JsonToken.BEGIN_ARRAY) //list or factoid
                          {
                              reader.beginArray();
                              
                                JsonToken peek1 = reader.peek();
                                ArrayList<String> listOfAnswers=new ArrayList<String>();
                                ArrayList<ArrayList<String>> listofarrays = new ArrayList<ArrayList<String>>();
                                
                                if(peek1==JsonToken.BEGIN_ARRAY) // list (or factoid-list since BioASQ3)
                                {
                                    /*
                                     * Warning: changed the following for BioASQ 5
                                     * No synonyms in submissions anymore, only in gold files
                                    */
                                    if(VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ2 || VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ3){
                                        listofarrays = readExactAnswerListOfArraysv2(reader);
                                        ea.setLists(listofarrays);
                                    } else if(VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ5 || VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ8){
                                        if(!this.isGold){ // For submissions use restricted parsing : only first of synonyms taken into account
                                            listofarrays = readExactAnswerListOfArraysv3(reader);
                                        } else { // For golden read all synonyms normally
                                            listofarrays = readExactAnswerListOfArraysv2(reader);                                            
                                        }
                                        ea.setLists(listofarrays);
                                    } else
                                    {
                                        System.out.println("Wrong challenge version. I will exit.");
                                        System.exit(0);
                                    }
                                }
                                else if(peek1 == JsonToken.STRING) // factoid (for BioASQ1&2)
                                {
                                    /*
                                     * Warning: changed the following for BioASQ 3
                                     * we now have list of arrays for factoid 
                                    */
                                    if(VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ2){
                                        listOfAnswers = readExactAnswerArray(reader);
                                        ea.setAnswers(listOfAnswers);
                                    }
                                    //not reached!
                                    else if(VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ3){
                                        listofarrays = readExactAnswerListOfArraysv2(reader);
                                        ea.setLists(listofarrays);
                                    }
                                    /*
                                     * Warning: changed the following for BioASQ 5
                                     * No synonyms are submitted anymore by participants
                                    */
                                    //not reached!
                                    else if(VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ5 || VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ8){
                                        listofarrays = readExactAnswerListOfArraysv3(reader);
                                        ea.setLists(listofarrays);
                                    }
                                    else
                                    {
                                        System.out.println("Wrong challenge version. I will exit.");
                                        System.exit(0);
                                    }
                                }
                              
                              //ea.setAnswers(listOfAnswers);
                              qst.setExact_answer(ea);
                              reader.endArray();
                              
                          }
                          else if(peek == JsonToken.STRING) //yesno
                          {
                              String yesno_answer = reader.nextString();
                              yesno_answer = yesno_answer.toLowerCase();
                              if(yesno_answer.contains("yes"))  
                                ea.setAnswer("yes");
                              else if(yesno_answer.contains("no"))  
                                ea.setAnswer("no");
                              else
                              {
				  ea.setAnswer("none");
                                //  System.out.println("Unknown answer in yesno question: "+yesno_answer);
                              }
                              qst.setExact_answer(ea);
                          }
                         
                      }
                      
//                      Edited for BioASQ4 Evaluation (to solve format conflict with Rouge.py)            
//                      ideal answers are not evaluated with this code, so no need to read them(Rouge and manual queration is used instead)

//                      else if(name.equals("ideal_answer"))
//                      {
//                          String ideal="";
//                          try{ideal = reader.nextString();}catch(IllegalStateException ex){System.out.println(ex.toString());System.out.println(jsonFile);
//                          }
//                          qst.setIdeal_answer(ideal);
//                      }
                      else if(name.equals("snippets"))
                      {
                          ArrayList<Snippet> snippets = readSnippets(reader);
                          qst.addSnippets(snippets);
                      }
                      else
                      {
                          reader.skipValue();
                          
                      }
                  }
                  //reader.skipValue();
                reader.endObject();  
                this.questions.add(qst);
                
          }
            reader.endArray();
            }
            else
                      {
                          reader.skipValue();
                          
                      }
        }
         reader.endObject();
       /*     System.out.println("Number of questions:"+num_questions);
            System.out.println("Number of triples:"+num_triples);
            System.out.println("Number of yesno:"+type_yesno);
            System.out.println("Number of factoid:"+type_factoid);
            System.out.println("Number of list:"+type_list);
            System.out.println("Number of summary:"+type_summary);*/

        } catch (FileNotFoundException ex) {
	    System.out.println("Problem in JSONfile : "+jsonFile);    
        }
        
    }

    
    private ArrayList<String> readExactAnswerArray(JsonReader reader)
    {
        ArrayList<String> answers = new ArrayList<String>();
        int count = 0;
        try {
            
            while (reader.hasNext()) {
                String nextString = reader.nextString();
                answers.add(nextString.toLowerCase());
            }
            
        } catch (IOException ex) {
        }

        return answers;
    }
    
    
    private ArrayList<String> readExactAnswerListOfArrays(JsonReader reader)
    {
        ArrayList<String> answers = new ArrayList<String>();
    
        int count = 0;
        try {
            while(reader.hasNext()){
            reader.beginArray();
            while (reader.hasNext()) {
                ArrayList<String> temp_ans = readExactAnswerArray(reader);
                answers.addAll(temp_ans);
            }
            reader.endArray();
            }
        } catch (IOException ex) {
        }

        return answers;
    }
 
    /** Reads exact answers submitted by systems for list [1] and factoid [2] questions 
     *      Also reads gold exact answers for list and factoid questions ( where synonyms included in BioASQ5 too)
     *      [1] Used for list questions, up to BioASQ4, when synonyms where submitted by participants
     *      [2] Used for factoid questions, of BioASQ3&4, when synonyms where submitted by participants
     */
    private ArrayList<ArrayList<String>> readExactAnswerListOfArraysv2(JsonReader reader)
    {
        ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();
    
        int count = 0;
        try {
            while(reader.hasNext()){
            reader.beginArray();
            while (reader.hasNext()) {
                ArrayList<String> temp_ans = readExactAnswerArray(reader);
                answers.add(temp_ans);
            }
            reader.endArray();
            }
        } catch (IOException ex) {
        }
//        System.out.println(answers);
        return answers;
    }
 
    /** Reads exact answers submitted by systems for list questions and factoid questions 
     *      Used since BioASQ 5, where no synonyms where submitted by participants  
     *      Only the first element of the inner list is taken into account for evaluation
     *      Note: Not used for golden exact answers, where synonyms included
     */
    private ArrayList<ArrayList<String>> readExactAnswerListOfArraysv3(JsonReader reader)
    {
        ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();
    
        int count = 0;
        try {
            while(reader.hasNext()){
            reader.beginArray();
            while (reader.hasNext()) {
                ArrayList<String> temp_ans = readExactAnswerArray(reader); // Full answer submitted (with possible synonyms)
                ArrayList<String> temp_ans_fisrt_item = new ArrayList<String>(); // edited answer (only fisrt synonym kept)
                if(!temp_ans.isEmpty()){
                    temp_ans_fisrt_item.add(temp_ans.get(0));
                }
                answers.add(temp_ans_fisrt_item);
            }
            reader.endArray();
            }
        } catch (IOException ex) {
        }
//        System.out.println(answers);
        return answers;
    }
    
    private ArrayList<Triple> readTriplesArray(JsonReader reader){
    
        ArrayList<Triple> triples = new ArrayList<Triple>();
        
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                
                reader.beginObject();
                String op="",pre="",sub="";
                while(reader.hasNext())
                  {
                      String name = reader.nextName();
                      if(name.equals("o"))
                      {
                        JsonToken peek = reader.peek();
                        if(peek.equals(JsonToken.NULL)){
                            op = "";reader.nextNull();}
                        else
                          op = reader.nextString();
                      }
                      else if(name.equals("p"))
                      {
                          pre = reader.nextString();
                      }
                      else if(name.equals("s"))
                      {
                          JsonToken peek = reader.peek();
                        if(peek.equals(JsonToken.NULL)){
                            sub="";reader.nextNull();
                          
                        }
                        else
                            sub = reader.nextString();
                      }
                      else
                          reader.skipValue();
                  }
                Triple tr = new Triple(pre, sub, op);
                reader.endObject();
                triples.add(tr);
            }
            reader.endArray();
        } catch (IOException ex) {
        }

        return triples;
   }
    
    
    private ArrayList<Snippet> readSnippets(JsonReader reader) {
         ArrayList<Snippet> snippets = new ArrayList<Snippet>();
        
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                
                reader.beginObject();
                String document="",fnameBegin="",fnameEnd="",text="";
                int beginIndex=0;
                int endIndex=0;
                
                while(reader.hasNext())
                  {
                      String name = reader.nextName();
                      if(name.equals("offsetInBeginSection"))
                      {
                          beginIndex = reader.nextInt();
                      }
                      else if(name.equals("offsetInEndSection"))
                      {
                          endIndex = reader.nextInt();
                      }
                      else if(name.equals("document"))
                      {
                          document = reader.nextString();
                      }
                      else if(name.equals("beginSection"))
                      {
                          fnameBegin = reader.nextString();
			  fnameBegin = fnameBegin.substring(fnameBegin.indexOf('.')+1);
                      }
                      else if(name.equals("endSection"))
                      {
                          fnameEnd = reader.nextString();
			  fnameEnd = fnameEnd.substring(fnameEnd.indexOf('.')+1);
                      }
                      else if(name.equals("text"))
                      {
                          text = reader.nextString();
                      }
                      else
                      {
                          //System.out.println("Unknown field "+name +" in snippet");
                          
                      }
                  }
                Snippet sn = new Snippet(document, text, fnameBegin, fnameEnd, beginIndex, endIndex);
                reader.endObject();
                snippets.add(sn);
            }
            reader.endArray();
        } catch (IOException ex) {
        }

        return snippets;
    }
    
    
   public ArrayList<String> readConcepts(JsonReader reader){
     
       ArrayList<String> conc = new ArrayList<String>();
        int count=0;
      try{
     reader.beginArray();
     while (reader.hasNext()) {
      String nextString = reader.nextString();
      if(!conc.contains(nextString))
       conc.add(nextString);
     }
     reader.endArray();
     }catch(IOException ex){}
        
      return conc;
     
   }
   
   public ArrayList<String> readDocuments(JsonReader reader){
     
       ArrayList<String> docs = new ArrayList<String>();
        int count=0;
      try{
     reader.beginArray();
     while (reader.hasNext()) {
      String nextString = reader.nextString();
      if(!docs.contains(nextString))
        docs.add(nextString);
     }
     reader.endArray();
     }catch(IOException ex){}
        
      return docs;
     
   }
   
   public Question getQuestion(String id)
   {
       for(int i=0;i<questions.size();i++)
       {
           Question qst = questions.get(i);
           if(qst.getId().equals(id))
               return qst;
       }
       
       return null;
   }

    public Question getQuestion(int index)
   {
       
     return questions.get(index);
           
     
   }

   
   public int numQuestions()
   {
       return questions.size();
   }
   
   public void dataProperties()
   {
       int docs=0,conc=0,snip=0;
       for(int i=0;i<questions.size();i++)
       {
           docs += questions.get(i).numOfDocs();
           conc += questions.get(i).numOfConcepts();
           snip += questions.get(i).numOfSnippets();
       }
       
       System.out.println("Avrg docs: "+ (double)docs/(double)questions.size());
       System.out.println("Avrg concepts: "+ (double)conc/(double)questions.size());
       System.out.println("Avrg snippets: "+ (double)snip/(double)questions.size());


   }
   
   
    public static void main(String args[])
    {
        Task1bData data = new Task1bData(2, false);
        try {
            data.readData(args[0]);
            data.dataProperties();
        } catch (IOException ex) {
            Logger.getLogger(Task1bData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

 


}

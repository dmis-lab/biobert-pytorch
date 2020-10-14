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

import java.util.ArrayList;

public class Question {
    String body;
    ArrayList<String> concepts;
    ArrayList<String> documents;
    ExactAnswer exact_answer;
    String id;
    String ideal_answer;
    ArrayList<Snippet> snippets;
    int type;
    ArrayList<Triple> triples;
    
    public static final int 
            FACTOID=1, YESNO=2,SUMMARY=3,LIST=4;

    public Question(String id, int type) {
        this.id = id;
        this.type = type;
        concepts = new ArrayList<String>();
        documents = new ArrayList<String>();
        snippets = new ArrayList<Snippet>();
        triples = new ArrayList<Triple>();
    }

    public Question() {
        concepts = new ArrayList<String>();
        documents = new ArrayList<String>();
        snippets = new ArrayList<Snippet>();
        triples = new ArrayList<Triple>();
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setExact_answer(ExactAnswer exact_answer) {
        this.exact_answer = exact_answer;
    }

    public ArrayList<String> getConcepts() {
        return concepts;
    }

    public ArrayList<String> getDocuments() {
        return documents;
    }

    public ExactAnswer getExact_answer() {
        return exact_answer;
    }

    public String getId() {
        return id;
    }

    public String getIdeal_answer() {
        return ideal_answer;
    }

    public ArrayList<Snippet> getSnippets() {
        return snippets;
    }

    public ArrayList<Triple> getTriples() {
        return triples;
    }

    public int getType() {
        return type;
    }

    public void setIdeal_answer(String ideal_answer) {
        this.ideal_answer = ideal_answer;
    }
    
    public void addSnippet(Snippet sn)
    {
        snippets.add(sn);
    }
    
    public void addSnippets(ArrayList<Snippet> sn)
    {
        snippets.addAll(sn);
    }
    
    public void addTriple(Triple tr)
    {
        triples.add(tr);
    }
    
    public void addTriples(ArrayList<Triple> tr)
    {
        triples.addAll(tr);
    }
    
    public void addDocument(String doc)
    {
        documents.add(doc);
    }
    
    public void addDocuments(ArrayList<String> docs)
    {
        documents.addAll(docs);
    }
    
    public void addConcept(String con)
    {
        concepts.add(con);
    }
    
    public void addConcepts(ArrayList<String> conc)
    {
        concepts.addAll(conc);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }
   
    public int numOfConcepts()
    {
        return this.concepts.size();
    }
   
    public int numOfDocs()
    {
        return this.documents.size();
    }
    
    public int numOfSnippets()
    {
        return this.snippets.size();
    }
    
    public boolean hasQuestionConcepts()
    {
        if(concepts.size()>0)
            return true;
        return false;
    }
}

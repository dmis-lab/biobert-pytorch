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
package evaluation;

import data.CalculatedMeasures;
import data.ExactAnswer;
import data.Question;
import data.Snippet;
import java.util.ArrayList;
/**
 * A class with "question-level measures"
 *      An object of this class is used to represent each submitted question during test-set-level measure calculation. 
 * @author tasosnent
 */
public class QuestionAnswerEvaluator {
    // Phase A question-level measures
    CalculatedMeasures concepts;
    CalculatedMeasures articles;
    CalculatedMeasures triples;
    CalculatedMeasures snippets;
    // Phase B question-level measures
    CalculatedMeasures exact_answers;
    
    String question_id;
    int question_type;
    Boolean is_yes=false; // Flag: when true this question is a yesno question and its golden answer is "yes"
    Boolean is_triple=false; // Flag: when true this question has at leat one golden triple
    Boolean has_concepts=false; // Flag: when true this question has at leat one golden concept
    int VERSION_OF_CHALLENGE;  // Use version 2 for BioASQ1&2, version 3 for BioASQ3&4, version 5 since BioASQ5, version 8 since BioASQ8

    /**
     * Constructor for phase A
     * @param id        question id
     * @param version   the version of the challenge
     * @param fl        This is not used. TODO: Delete this parameter
     */
    public QuestionAnswerEvaluator(String id,int version)
    {
        concepts= new CalculatedMeasures();
        articles=new CalculatedMeasures();
        triples=new CalculatedMeasures();
        snippets=new CalculatedMeasures();
        question_id = id;
        VERSION_OF_CHALLENGE = version;
    }
    
    /**
     * Constructor for phase B
     * @param id        question id
     * @param qt        question type
     * @param version   the version of the challenge
     */
    public QuestionAnswerEvaluator(String id,int qt,int version)
    {
        exact_answers = new CalculatedMeasures();
        question_id = id;
        question_type = qt;
        VERSION_OF_CHALLENGE = version;
    }
    
    /**
     * Calculate Phase B question-level evaluation measures depending on the corresponding question type
     *  and update corresponding CalculatedMeasures object (i.e. exact_answers)
     * @param golden    golden question
     * @param response  submitted question
     */
    public void calculatePhaseBMeasuresForPair(Question golden,Question response)
    {
        if(question_type == Question.FACTOID)
        {
            if(this.VERSION_OF_CHALLENGE == evaluation.EvaluatorTask1b.BIOASQ2){
                strictAccuracy(golden.getExact_answer(),response.getExact_answer(),exact_answers);
                lenientAccuracy(golden.getExact_answer(),response.getExact_answer(),exact_answers);
                meanReciprocalRank(golden.getExact_answer(),response.getExact_answer(),exact_answers);
            } // Since BioASQ3 up to five answers can be submitted for factoid questions
            else if(this.VERSION_OF_CHALLENGE==evaluation.EvaluatorTask1b.BIOASQ3 || this.VERSION_OF_CHALLENGE==evaluation.EvaluatorTask1b.BIOASQ5 || this.VERSION_OF_CHALLENGE==evaluation.EvaluatorTask1b.BIOASQ8)
            {
                strictAccuracyForLists(golden.getExact_answer(),response.getExact_answer(),exact_answers);
                lenientAccuracyForLists(golden.getExact_answer(),response.getExact_answer(),exact_answers);
                meanReciprocalRankForLists(golden.getExact_answer(),response.getExact_answer(),exact_answers);
            }
        }
        else if(question_type == Question.LIST)
        {
            calculatePRFforListQuestions(golden.getExact_answer(),response.getExact_answer(),exact_answers);
        }
        else if(question_type == Question.YESNO)
        {
            // find accuracy (i.e. if this is a true prediction - yes or no)
            accuracyYesNo(golden.getExact_answer(),response.getExact_answer(),exact_answers);
            // Also store the correct label - yes or no - for label based evaluation (F1-yes, F1-no and macro F1)
            this.is_yes = golden.getExact_answer().getAnswer().equalsIgnoreCase("yes");
        }
    }
    
    /**
     * Calculate Phase A question-level evaluation measures
     *  and update corresponding CalculatedMeasures objects (i.e. articles, snippets etc)
     *  for concepts and snippets also take into account questions not having any golden concepts/snippets at all
     * @param golden    golden question
     * @param response  submitted question
     */
    public void calculateMeasuresForPair(Question golden,Question response)
    {try{
        if(golden.getConcepts().size()>0 && !response.getConcepts().isEmpty())
        {
            calculatePRF(golden.getConcepts(), response.getConcepts(), concepts);
            has_concepts = true;
        }}
        catch(Exception ex){System.out.println(ex.toString());System.out.println(golden.getId());}
        
        
        calculatePRF(golden.getDocuments(), response.getDocuments(), articles);
        if(!golden.getTriples().isEmpty())
        {
            calculatePRF(golden.getTriples(), response.getTriples(), triples);
            is_triple = true;
        }
        
        concatenateSnippets(golden.getSnippets());
        concatenateSnippets(response.getSnippets());
        
        calculatePRForSnippets(golden.getSnippets(), response.getSnippets(),snippets);
        // Why existence of concepts isn't checked similarly to triples? ( calculateAveragePrecision internally handles this case by assigning 0 Average precision)
        // TODO: add check for concept existence
//        if(!golden.getConcepts().isEmpty())
            calculateAveragePrecision(golden.getConcepts(), response.getConcepts(), concepts);
        
        calculateAveragePrecision(golden.getDocuments(), response.getDocuments(), articles);
        
        if(!golden.getTriples().isEmpty())
            calculateAveragePrecision(golden.getTriples(), response.getTriples(), triples);     
        
        calculateAveragePrecisionSnippets(golden.getSnippets(), response.getSnippets(), snippets);
    }
    
    /** Phase A Measures **/
    
    /**
     * Calculate Precision, Recall and Fmeasure for snippets of this submission
     *  and update the cm object accordingly (i.e. snippets)
     * @param listGolden        golden snippets
     * @param listResponses     submitted snippets
     * @param cm                question-level measures object to store the results (i.e. snippets)
     */
    public void calculatePRForSnippets(ArrayList<Snippet> listGolden, ArrayList<Snippet> listResponses, CalculatedMeasures cm)       
    {
        if(listResponses.isEmpty())
        {
            return;
        }
        
        int resp_size=0;
        int total_overlap=0;
        int g_size=0;
        int skippeddocs=0;
        
        for(int i=0;i<listResponses.size();i++)
        {
            
            Snippet sn = listResponses.get(i);
            /*if(listPubMedCentral.containsKey(sn.getDocumentOnlyID())){ // skip the documents that come from PubMedCentral
                 skippeddocs++;   continue;
            }*/
            
         //   if(sn.getSize()<0)
          //  {System.out.println(this.question_id);System.out.println(skippeddocs);System.exit(0);
          //  }
            resp_size += sn.getSize();
            int docsfound=0;
            for(int j=0;j<listGolden.size();j++)
            {
                Snippet g = listGolden.get(j);
               // if(listPubMedCentral.containsKey(g.getDocumentOnlyID())) // skip the documents that come from PubMedCentral
               //     continue;
                if(sn.getDocumentOnlyID().equals(g.getDocumentOnlyID())) // we can have more than one snippet per document and per paragraph
                {docsfound++;
                    total_overlap += sn.overlap(g);
                }
            }
         //   System.out.println("Docs found: "+docsfound +" question: "+this.question_id +" doc: "+sn.getDocument());
           // System.out.println("Total overlap :" + total_overlap);
        }
        
        for(int j=0;j<listGolden.size();j++)
         {
            Snippet g = listGolden.get(j);
            //  if(listPubMedCentral.containsKey(g.getDocumentOnlyID())) // skip the documents that come from PubMedCentral
             //       continue;
            g_size+=g.getSize();
         }
        
     //   System.out.println("Total overlap :" + total_overlap +" Resp size: "+resp_size +" gold: "+g_size);
        if(resp_size != 0)
        cm.setPrecision((double)total_overlap/((double)resp_size));
        if(g_size!=0)
        cm.setRecall((double)total_overlap/(double)g_size);
        if(cm.getPrecision()!=0 || cm.getRecall()!=0)
           cm.setFmeasure(2*cm.getPrecision()*cm.getRecall()/(cm.getPrecision()+cm.getRecall()));
    }
    /**
     * Calculate Precision, Recall and Fmeasure for elements (except snippets: i.e. for documents, concepts or triples) of this submission
     *  and update the corresponding cm object accordingly
     * @param listGolden        golden elements (e.g. documents)
     * @param listResponses     submitted elements (e.g. documents)
     * @param cm                question-level measures object to store the results (e.g. articles)
     */
    public void calculatePRF(ArrayList listGolden, ArrayList listResponses, CalculatedMeasures cm)
    {
        double tp=0,fp=0,fn=0;
        
        if(listResponses.isEmpty())
        {
            return;
        }
        
        for(int i=0;i<listResponses.size();i++)
        {
            Object item = listResponses.get(i);
            if(listGolden.contains(item))
                tp++;
            else
            {
                fp++;
            }
        }

        for(int i=0;i<listGolden.size();i++)
        {
            Object item = listGolden.get(i);
            if(!listResponses.contains(item))
                fn++;
        }

        cm.setPrecision(tp/(tp+fp));
      
        if((fn+tp)!=0)
            cm.setRecall(tp/(tp+fn));
        
        if(cm.getPrecision()!=0 && cm.getRecall()!=0)
            cm.setFmeasure(2*cm.getPrecision()*cm.getRecall()/(cm.getPrecision()+cm.getRecall()));
    }
    /**
     * Calculate Average Precision for this answer - list of elements (documents, concepts, triples - not snippets)
     *  and update the corresponding cm object accordingly
     * @param listGolden        golden elements (e.g. documents)
     * @param listResponses     submitted elements (e.g. documents)
     * @param cm                question-level measures object to store the results (e.g. articles)
     */
    public void calculateAveragePrecision(ArrayList listGolden, ArrayList listResponses, CalculatedMeasures cm)
    {
        double ap=0;
        
        for(int i=0;i<listResponses.size();i++)
        {
            ap+=precisionAtRfirstItems(i+1, listGolden, listResponses)*relevance(listResponses.get(i), listGolden);
        }
        // If none of the response elements is corect, 0 is returned.
        // This also handles the case that the golden list is empty! (i.e. correct responses will alsways be 0 in this case)
        listResponses.retainAll(listGolden);
         if(listResponses.isEmpty()){
             cm.setAverage_precision(0);
             return;
         }
         
        // ** UPDATE 17/02/2015 : in BioASQ 3 we divide with 10. Please
        //    check the guidlines **
         if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ2)
	 // we should divide with the size of the golden list             
            cm.setAverage_precision(ap/(double)listGolden.size());
         else if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ3 || this.VERSION_OF_CHALLENGE==evaluation.EvaluatorTask1b.BIOASQ5)
            cm.setAverage_precision(ap/10.0);
         else if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ8)
            {cm.setAverage_precision(ap/ Math.min(10.0,(double)listGolden.size()));
            }
    }
    /**
     * Calculate Precision at R first items for this answer - list of elements (documents, concepts etc)
     *  Used for MAP calculation
     * @param r                 number of element to be taken into account 
     * @param listGolden        golden elements (e.g. documents)
     * @param listResponses     submitted elements (e.g. documents)
     * @return                  precision of submitted list taking into account r first elements submitted
     */
    public double precisionAtRfirstItems(int r,ArrayList listGolden, ArrayList listResponses)
    {
        double tp=0,fp=0;
        
        if(listResponses.isEmpty())
        {
            return 0;
        }
        
        for(int i=0;i<r;i++)
        {
            Object item = listResponses.get(i);
            if(listGolden.contains(item))
                tp++;
            else
            {
                fp++;
            }
        }
        if((tp+fp)==0)
            return 0;
        return tp/(tp+fp);
    }
    /**
     * Calculate relevance of a submitted item (e.g. document or concept etc) i.e. if this document is included in the golden list
     *  Used for MAP calculation
     * @param item          The item to be checked for relevance
     * @param listGolden    the golden list to check if contains the item
     * @return              1 if contained in golden list (i.e. is relevant) 0 if not
     */
    public int relevance(Object item,ArrayList listGolden)
    {
        if(listGolden.contains(item))
            return 1;
        return 0;
    }
    /**
     * Calculate Average Precision for this list of Snippets submitted
     *  and update the corresponding cm object accordingly
     * @param listGolden        golden Snippets
     * @param listResponses     submitted Snippets 
     * @param cm                question-level measures object to store the results (i.e. snippets)
     */
    public void calculateAveragePrecisionSnippets(ArrayList<Snippet> listGolden, ArrayList<Snippet> listResponses, CalculatedMeasures cm)
    {
        double ap=0;
        for(int i=0;i<listResponses.size();i++)
        {
            ap+=precisionAtRSnippet(i+1, listGolden, listResponses)*relevanceSnippet(listResponses.get(i), listGolden);
        }
        
         // ** UPDATE 17/02/2015 : in BioASQ 3 we divide with 10. Please
        //    check the guidlines **
        if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ2)
            cm.setAverage_precision(ap/(double)listGolden.size());
        else if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ3 || this.VERSION_OF_CHALLENGE==evaluation.EvaluatorTask1b.BIOASQ5)
            cm.setAverage_precision(ap/10.0);
        else if(VERSION_OF_CHALLENGE==EvaluatorTask1b.BIOASQ8)
            {cm.setAverage_precision(ap/ Math.min(10.0,(double)listGolden.size()));
            }
    }
    /**
     * Calculate Precision at R first snippets of this list
     *  Used for MAP calculation
     * @param r                 number of snippets to be taken into account 
     * @param listGolden        golden snippets
     * @param listResponses     submitted snippets
     * @return                  precision of submitted list taking into account r first snippets submitted
     */
    public double precisionAtRSnippet(int r,ArrayList<Snippet> listGolden, ArrayList<Snippet> listResponses)
    {
        if(listResponses.isEmpty())
        {
            return 0;
        }
        
        int resp_size=0;
        int total_overlap=0;
        int g_size=0;
        
        for(int i=0;i<r;i++)
        {
            Snippet sn = listResponses.get(i);
            resp_size += sn.getSize();
            
            for(int j=0;j<listGolden.size();j++)
            {
                Snippet g = listGolden.get(j);
                if(sn.getDocument().equals(g.getDocument()))
                {
                    total_overlap += sn.overlap(g);
                }
            }
        }
        
        for(int j=0;j<listGolden.size();j++)
         {
            Snippet g = listGolden.get(j);
            g_size+=g.getSize();
         }
        
        return (double)total_overlap/((double)resp_size);
    }
    /**
     * Calculate relevance of a submitted snippet i.e. if is included in the golden list
     *  Used for MAP calculation
     * @param ret          The Snippet to be checked for relevance
     * @param listGolden    the golden list to check if contains the snippet
     * @return              1 if  the snippet overlaps with one contained in golden list (i.e. is relevant) 0 if not
     */
    private double relevanceSnippet(Snippet ret, ArrayList<Snippet> listGolden) {
            for(int j=0;j<listGolden.size();j++)
            {
                Snippet g = listGolden.get(j);
                if(ret.getDocument().equals(g.getDocument()))
                {
                    if(ret.overlap(g)!=0);
                        return 1;
                }
            }
         return 0;
    }
    /**
     * Concatenate all snippets in the provided list per document if they overlap.
     * @param listsnip 
     */
    public void concatenateSnippets(ArrayList<Snippet> listsnip)
    {
        if(listsnip.isEmpty())
        {
            return;
        }
        
        
        for(int i=0;i<listsnip.size();i++)
        {
            
            for(int j=0;j<listsnip.size();j++)
            {
                if(j==i)
                    continue;
                if(listsnip.get(i).getDocument().equals(listsnip.get(j).getDocument()))
                {
                    if(listsnip.get(i).getFieldNameBegin().equals(listsnip.get(j).getFieldNameBegin())&&
                            listsnip.get(i).getFieldNameEnd().equals(listsnip.get(j).getFieldNameEnd()))
                    {
                        if(listsnip.get(i).itOverlaps(listsnip.get(j))) // merge snippets
                        {
                            Snippet merged = listsnip.get(i).merge(listsnip.get(j));
                            listsnip.remove(i);
                            listsnip.add(i, merged);
                            listsnip.remove(j);
                           // System.out.println("Merging "+listsnip.get(i).getDocument());
                           // System.out.println(merged.getBegin_index()+" "+merged.getEnd_index());
                            j=0;
                        }
                    }
                }
            }
        }
        
    }

    /** Phase B Measures **/
    
    /**
     * Calculate Precision, Recall and Fmeasure for this list question answer 
     *  and update the corresponding cm object accordingly
     * @param golden        golden exact answer
     * @param response      submitted excact answer
     * @param cm            question-level measure object to store the results (i.e. exact_answers)
     */
    public void calculatePRFforListQuestions(ExactAnswer golden,ExactAnswer response, CalculatedMeasures cm)
    {
        double tp=0,fp=0,fn=0;
        
        if(response==null||response.getLists().isEmpty())
        {
            return;
        }
        
        for(int i=0;i<response.getLists().size();i++)
        {
	    // check if the answer has a synonym
            if(golden.containsAnswerSynonym(response.getLists().get(i),true))
            {
                tp++;
            }
            else
            {
                fp++;
            }
        }

        for(int i=0;i<golden.getLists().size();i++)
        {
            if(!response.containsAnswerSynonym(golden.getLists().get(i),true))
                fn++;
        }

        //System.out.println("TP: "+tp+"  FP: "+fp +" FN: "+fn);
        cm.setPrecision(tp/(tp+fp));
        if((fn+tp)!=0)
            cm.setRecall(tp/(tp+fn));
        
        if(cm.getPrecision()!=0 && cm.getRecall()!=0)
            cm.setFmeasure(2*cm.getPrecision()*cm.getRecall()/(cm.getPrecision()+cm.getRecall()));
    }
    /**Assign Accuracy for the specific submitted YesNo question
     * 
     * @param exact_answer  golden answer
     * @param response      submitted answer
     * @param cm            object to store measures
     */
    private void accuracyYesNo(ExactAnswer exact_answer, ExactAnswer response,CalculatedMeasures cm) {
	
	if(response==null||response.getAnswer().isEmpty()||response.getAnswer()==null)
	{	    
	    cm.setAccuracy(0.0);return;
	}
        if(exact_answer.getAnswer().equals(response.getAnswer()))
            cm.setAccuracy(1.0);
    }
    
    private void strictAccuracy(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        ArrayList<String> answers_golden = gold_answer.getAnswers();
        ArrayList<String> answers_system = system_answer.getAnswers();
       
	if(answers_system.isEmpty()||answers_golden.isEmpty())
	{
            exact_answers.setStrict_accuracy(0.0);
	    return;
	}
        if(answers_system.get(0).equals(answers_golden.get(0)))
            exact_answers.setStrict_accuracy(1.0);
    }
    
    private void strictAccuracyForLists(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        
        ArrayList<ArrayList<String>> listsOfFactAnswers = system_answer.getLists();
        //check for emptyness of list added 
        if(!listsOfFactAnswers.isEmpty() && gold_answer.containsAnswerSynonym(listsOfFactAnswers.get(0),false)){
                exact_answers.setStrict_accuracy(1.0);
                return;
            }
        
        
        exact_answers.setStrict_accuracy(0.0);
    }

    private void lenientAccuracyForLists(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        
        
        ArrayList<ArrayList<String>> listsOfFactAnswers = system_answer.getLists();
        
        for(ArrayList<String> ans_system : listsOfFactAnswers)
        {
            if(gold_answer.containsAnswerSynonym(ans_system,false)){
                exact_answers.setLenient_accuracy(1.0);
                return;
            }
        }
        
        
    }
    
    private void lenientAccuracy(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        ArrayList<String> answers_golden = gold_answer.getAnswers();
        ArrayList<String> answers_system = system_answer.getAnswers();
        
        for(int i=0;i<answers_system.size();i++){
            for(int j=0;j<answers_golden.size();j++){
                if(answers_system.get(i).equals(answers_golden.get(j)))
                {
                    exact_answers.setLenient_accuracy(1.0);
                    return;
                }
                
            }
        }
    }

    private void meanReciprocalRank(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        ArrayList<String> answers_golden = gold_answer.getAnswers();
        ArrayList<String> answers_system = system_answer.getAnswers();
        
        for(int i=0;i<answers_system.size();i++){
            for(int j=0;j<answers_golden.size();j++){
                if(answers_system.get(i).equals(answers_golden.get(j)))
                {
                    exact_answers.setMean_reciprocal_rank(1.0/(double)(i+1));
                    //System.out.println(1.0/(double)(i+1));
                    return;
                }
                
            }
        }
    }

    private void meanReciprocalRankForLists(ExactAnswer gold_answer, ExactAnswer system_answer, CalculatedMeasures exact_answers) {
	if(system_answer==null)
	    return;
        
        ArrayList<ArrayList<String>> listsOfFactAnswers = system_answer.getLists();
        
        for(int i=0;i<listsOfFactAnswers.size();i++)
        {
            
            if(gold_answer.containsAnswerSynonym(listsOfFactAnswers.get(i),false)){
                exact_answers.setMean_reciprocal_rank(1.0/(double)(i+1));
                return;
            }
        }
        
    }
    
    public double getPrecisionEA()
    {
        return exact_answers.getPrecision();
    }

    public double getRecallEA()
    {
        return exact_answers.getRecall();
    }

    public double getF1EA()
    {
        return exact_answers.getFmeasure();
    }

    public double getAccuracyYesNo() {
        return exact_answers.getAccuracy();
    }

    public double getStrictAccuracy()
    {
        return exact_answers.getStrict_accuracy();
    }

    public double getLenientAccuracy()
    {
        return exact_answers.getLenient_accuracy();
    }

    public double getMRR()
    {
        return exact_answers.getMean_reciprocal_rank();
    }

    // ** Get and Set methods **
    
    /**
     * Get question id
     * @return question id
     */
    public String  getQuestionID()
            
    {
	   return question_id;
    } 
    public int getQuestion_type() {
        return question_type;
    }
    
    public double getConceptsPrecision()
    {
        return concepts.getPrecision();
    }

    public double getConceptsRecall()
    {
        return concepts.getRecall();
    }

    public double getConceptsF1()
    {
        return concepts.getFmeasure();
    }
    
    public double getArticlesPrecision()
    {
        return articles.getPrecision();
    }

    public double getArticlesRecall()
    {
        return articles.getRecall();
    }

    public double getArticlesF1()
    {
        return articles.getFmeasure();
    }

    public double getSnippetsPrecision()
    {
        return snippets.getPrecision();
    }

    public double getSnippetsRecall()
    {
        return snippets.getRecall();
    }

    public double getSnippetsF1()
    {
        return snippets.getFmeasure();
    }

    public double getTriplesPrecision()
    {
        return triples.getPrecision();
    }

    public double getTriplesRecall()
    {
        return triples.getRecall();
    }

    public double getTriplesF1()
    {
        return triples.getFmeasure();
    }
    
    public boolean hasQuestionConcepts()
    {
        return has_concepts;
    }
    
    public double getAveragePrecisionConcepts()
    {
        return concepts.getAverage_precision();
    }

    public double getAveragePrecisionDocuments()
    {
        return articles.getAverage_precision();
    }

    public double getAveragePrecisionTriples()
    {
        return triples.getAverage_precision();
    }

    public double getAveragePrecisionSnippets()
    {
        return snippets.getAverage_precision();
    }

    public double getF1Snippets()
    {
        return snippets.getFmeasure();
    }
}

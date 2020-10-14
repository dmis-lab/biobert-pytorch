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

import data.Question;
import data.Task1bData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/** This script called for BioASQ Task B evaluation, both Phases.
 * 
 *  Example calls
 *      Phase A
 *          java -cp BioASQEvaluation2018.jar evaluation.EvaluatorTask1b -phaseA -e 5 "...\golden.json" "...\submission_PhasA.json" -verbose
 *          or java -cp BioASQEvaluation2018.jar evaluation.EvaluatorTask1b -phaseA -e 5 "...\golden.json" "...\submission_PhasA.json"
 *      Phase B
 *          java -cp BioASQEvaluation2018.jar evaluation.EvaluatorTask1b -phaseB -e 5 "...\golden.json" "...\submission_PhasB.json" -verbose
 *          or java -cp BioASQEvaluation2018.jar evaluation.EvaluatorTask1b -phaseB -e 5 "...\golden.json" "...\submission_PhasB.json" 
 * 
 * @author tasosnent
 */
public class EvaluatorTask1b {
    
    Task1bData goldenData;
    Task1bData systemResp;
    double epsilon=0.00001;
    // The same as in Task1bData
    int VERSION_OF_CHALLENGE=8; // we use this to have modified versions of the measures for different BioASQ years
    // Use version 2 for BioASQ1&2, version 3 for BioASQ3&4, version 5 since BioASQ5,version 8 since BioASQ8 
    public static final int BIOASQ2=2,BIOASQ3=3,BIOASQ5=5,BIOASQ8=8;
    boolean verbosity = false;
    
    
    /**
     * Reads golden data and submission data from corresponding files
     * @param golden    golden file
     * @param system    submitted file, for evaluation
     * @param version   The version of the Challenge  // Use version 2 for BioASQ1&2, version 3 for BioASQ3&4, version 5 since BioASQ5,version 8 since BioASQ8 
     */
    public EvaluatorTask1b(String golden, String system,int version)
    {
        this.setVERSION_OF_CHALLENGE(version);
        //Golden data object
        goldenData = new Task1bData(version, true);
        //System responce object
        systemResp = new Task1bData(version, false);
        try {
            goldenData.readData(golden);
            systemResp.readData(system);
        } catch (IOException ex) {
            Logger.getLogger(EvaluatorTask1b.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Calculate evaluation measures for Phase A
     */
    public void EvaluatePhaseA()
    {
        // Question-level measures: An array with an evaluator object (with evaluation measures calculated) for each question of golden set
        ArrayList<QuestionAnswerEvaluator> qevalArray = new ArrayList<QuestionAnswerEvaluator>();
        
    //    System.out.println("Golden data: "+goldenData.numQuestions());
    //    System.out.println("System replies: "+systemResp.numQuestions());
       
        // For each question in golden data
        for(int i=0;i<goldenData.numQuestions();i++)
        {
            Question gold = goldenData.getQuestion(i);
            Question resp = systemResp.getQuestion(gold.getId());
            if(resp==null)
                continue;
            // Create an evaluator for this pair
            QuestionAnswerEvaluator qeval =new QuestionAnswerEvaluator(gold.getId(),this.VERSION_OF_CHALLENGE);
            // Calculate evaluation measures for phase B
            qeval.calculateMeasuresForPair(gold, resp);
            //put to qevalArray
            qevalArray.add(qeval);
        }
        
        // Now, give the array with "question-level measures" to calculate "set-level measures" (averaging) for each type of answer items:
        // concepts
        System.out.print(
            MeanPrecisionConcepts(qevalArray)+" "+
            MeanRecallConcepts(qevalArray)+" "+
            MeanF1Concepts(qevalArray)+" "+
            MapConcepts(qevalArray)+" "+
            GMapConcepts(qevalArray)+" ");
        // articles
        System.out.print(
            MeanPrecisionArticles(qevalArray)+" "+
            MeanRecallArticles(qevalArray)+" "+
            MeanF1Articles(qevalArray)+" "+
            MapDocuments(qevalArray)+" "+
            GMapDocuments(qevalArray)+" ");
        // snippets
        System.out.print(
            MeanPrecisionSnippets(qevalArray)+" "+
            MeanRecallSnippets(qevalArray)+" "+
            MeanF1Snippets(qevalArray)+" "+
            MapSnippets(qevalArray)+" "+
            GMapSnippets(qevalArray)+" ");
        // Triples
        System.out.print(
            MeanPrecisionTriples(qevalArray)+" "+
            MeanRecallTriples(qevalArray)+" "+
            MeanF1Triples(qevalArray)+" "+
            MapTriples(qevalArray)+" "+
            GMapTriples(qevalArray));

        if(this.verbosity){
            System.out.println();            
            System.out.println("MPrec concepts: "+MeanPrecisionConcepts(qevalArray));
            System.out.println("MRec concepts: "+MeanRecallConcepts(qevalArray));
            System.out.println("MF1 concepts: "+MeanF1Concepts(qevalArray));
            System.out.println("MAP concepts: "+MapConcepts(qevalArray));
            System.out.println("GMAP concepts: "+GMapConcepts(qevalArray));
            
            System.out.println("MPrec documents: "+MeanPrecisionArticles(qevalArray));
            System.out.println("MRec documents: "+MeanRecallArticles(qevalArray));
            System.out.println("MF1 documents: "+MeanF1Articles(qevalArray));
            System.out.println("MAP documents: "+MapDocuments(qevalArray));
            System.out.println("GMAP documents: "+GMapDocuments(qevalArray));

            System.out.println("MPrec snippets: "+MeanPrecisionSnippets(qevalArray));
            System.out.println("MRec snippets: "+MeanRecallSnippets(qevalArray));
            System.out.println("MF1 snippets: "+MeanF1Snippets(qevalArray));
            System.out.println("MAP snippets: "+MapSnippets(qevalArray));        
            System.out.println("GMAP snippets: "+GMapSnippets(qevalArray));

            System.out.println("MPrec triples: "+MeanPrecisionTriples(qevalArray));
            System.out.println("MRec triples: "+MeanRecallTriples(qevalArray));
            System.out.println("MF1 triples: "+MeanF1Triples(qevalArray));
            System.out.println("MAP triples: "+MapTriples(qevalArray));
            System.out.println("GMAP triples: "+GMapTriples(qevalArray));
        }
    }
    
    /**
     * Calculate evaluation measures for Phase B
     */
    public void EvaluatePhaseB()
    {
        // Question-level measures: An array with an evaluator object (with evaluation measures calculated) for each question of golden set
        ArrayList<QuestionAnswerEvaluator> qevalArray = new ArrayList<QuestionAnswerEvaluator>();
        // For each question in golden data
        for(int i=0;i<goldenData.numQuestions();i++)
        {
            Question gold = goldenData.getQuestion(i);
            Question resp = systemResp.getQuestion(gold.getId());
            
            if(resp==null) continue;
             // Create an evaluator for this pair
             QuestionAnswerEvaluator qeval =new QuestionAnswerEvaluator(gold.getId(), 
                     gold.getType(),this.VERSION_OF_CHALLENGE);
             // Calculate evaluation measures for phase B
             qeval.calculatePhaseBMeasuresForPair(gold, resp);
             //put to qevalArray
             qevalArray.add(qeval);
        }
        // Now, give the array with "question-level measures" to calculate "set-level measures" (averaging) 
        System.out.print(
                AccuracyExactAnswersYesNo(qevalArray)+" "
                +strictAccuracy(qevalArray)+" "
                +lenientAccuracy(qevalArray)+" "
                +meanReciprocalRank(qevalArray)+" "
                +listPrecision(qevalArray) +" "
                +listRecall(qevalArray)+" "
                +listF1(qevalArray)+" "
                +macroF1ExactAnswersYesNo(qevalArray)+" "
                +F1ExactAnswersYesNo(qevalArray,true)+" "
                +F1ExactAnswersYesNo(qevalArray,false));
        
        if(this.verbosity){
            System.out.println();
            System.out.println("YesNo Acc: "+AccuracyExactAnswersYesNo(qevalArray));
            System.out.println("Factoid Strict Acc: "+strictAccuracy(qevalArray));
            System.out.println("Factoid Lenient Acc: "+lenientAccuracy(qevalArray));
            System.out.println("Factoid MRR: "+meanReciprocalRank(qevalArray));
            System.out.println("List Prec: "+listPrecision(qevalArray));
            System.out.println("List Rec: "+listRecall(qevalArray));
            System.out.println("List F1: "+listF1(qevalArray));
            System.out.println("YesNo macroF1: "+macroF1ExactAnswersYesNo(qevalArray));
            System.out.println("YesNo F1 yes: "+F1ExactAnswersYesNo(qevalArray,true));
            System.out.println("YesNo F1 no: "+F1ExactAnswersYesNo(qevalArray,false));
        }
    }
    
    /** Phase B Measures **/
    
    /**
    * Calculate Accuracy for YesNo questions
    * @param qeval  Object with question-level evaluation measures
    * @return       Accuracy for YesNo questions
    */ 
    public double AccuracyExactAnswersYesNo(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0; // All Yes-No questions (Test-set size) : All Positive + All Negative [P+N]
        double m=0; // All true predicted : true positive + true negative [TP + TN]
        // For all questions in test-set
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.YESNO)
            {
                m+= qeval.get(i).getAccuracyYesNo();
                k++;
            }
        }
	if(k==0)
	    return 0;
        return m/k; 
    }
    /**
    * Calculate F1 measure for YesNo questions
    * @param qeval          Object with question-level evaluation measures
    * @param yes_label     label for the F1 measure: true for label "yes", false for label "no"
    * @return               F1 measure for given label for YesNo questions F1yes for yes_label = true, F1no for yes_label = false
    */ 
    public double F1ExactAnswersYesNo(ArrayList<QuestionAnswerEvaluator> qeval, boolean yes_label)
    {
        int k=0; // All Yes-No questions (Test-set size) : All Positive + All Negative [P+N]
        // A confusion martix 
        ConfusionMatrix cm = new ConfusionMatrix();
        
        // For all questions in test-set
        for(int i=0;i<qeval.size();i++)
        {
            // If it is a yes-no question
            if(qeval.get(i).getQuestion_type()==Question.YESNO)
            {
                if(qeval.get(i).is_yes == yes_label){ 
                // it is a "Positive example" (either yes or no, depending on label given)
                    if(qeval.get(i).getAccuracyYesNo() == 1){ // If accurate prediction, increase True positive 
                        cm.increaseTP();
                    } else { // Else, this positive example was predicted as negative
                        cm.increaseFP();
                    }
                } else { 
                // it is a "Negative example" (either yes or no, depending on label given)
                    if(qeval.get(i).getAccuracyYesNo() == 1){ // If accurate prediction, increase True negative
                        cm.increaseTN();
                    } else { // Else, this negative example was predicted as positive
                        cm.increaseFN();
                    }
                }
                k++;
            }
        }
        // F1 = 2TP / (2TP + FP + FN)
        double a = 2*(double)cm.getTp(); // 2 TP
        double b = (2*(double)cm.getTp() + (double)cm.getFp() + (double)cm.getFn()); // (2TP + FP + FN)
	if(k==0 || b==0)
	    return 0; // No YesNo questions found or all of them belong to the other label and were correctly predicted (TN)
        return a/b; // F1 = 2TP / (2TP + FP + FN)
    }
    /**
    * Calculate macro averaged F1 measure for YesNo questions
    * @param qeval      Object with question-level evaluation measures
    * @return           macro averaged F1 measure for YesNo questions
    */
    public double macroF1ExactAnswersYesNo(ArrayList<QuestionAnswerEvaluator> qeval){
        // macroF1 = (F1yes + F1no) / 2 
        return (F1ExactAnswersYesNo(qeval, true) + F1ExactAnswersYesNo(qeval, false)) / 2;
    }
    /**
     * Calculate strictAccuracy for factoid questions
     * @param qeval     Object with question-level evaluation measures
     * @return          strictAccuracy for factoid questions
     */
    public double strictAccuracy(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.FACTOID)
            {
                m+= qeval.get(i).getStrictAccuracy();
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return m/k;
    }
    /**
     * Calculate lenientAccuracy for factoid questions
     * @param qeval     Object with question-level evaluation measures
     * @return          lenientAccuracy for factoid questions
     */
    public double lenientAccuracy(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.FACTOID)
            {
                m+= qeval.get(i).getLenientAccuracy();
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return m/k;
    }
    /**
     * Calculate meanReciprocalRank for factoid questions
     * @param qeval     Object with question-level evaluation measures
     * @return          meanReciprocalRank for factoid questions
     */
    public double meanReciprocalRank(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.FACTOID)
            {
                m+= qeval.get(i).getMRR();
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return m/k;
    }
    /**
     * Calculate Precision for list questions
     * @param qeval     Object with question-level evaluation measures
     * @return          Precision for list questions
     */
    public double listPrecision(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double pre=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.LIST)
            {
	   if(Double.isNaN(qeval.get(i).getPrecisionEA()))
		   pre+=0;
	   else
                pre+= qeval.get(i).getPrecisionEA();
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return pre/k;
    }
    /**
     * Calculate Recall for list questions
     * @param qeval     Object with question-level evaluation measures
     * @return          Recall for list questions
     */
    public double listRecall(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double recall=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.LIST)
            {
                
	   if(Double.isNaN(qeval.get(i).getRecallEA()))
		   recall+=0;
	   else
                recall+= qeval.get(i).getRecallEA();
                
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return recall/k;
    }
    /**
     * Calculate F1 for list questions
     * @param qeval     Object with question-level evaluation measures
     * @return          F1 for list questions
     */   
    public double listF1(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        int k=0;
        double f1=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).getQuestion_type()==Question.LIST)
            {
               
		  //System.out.println(qeval.get(i).getF1EA());
		  if(Double.isNaN(qeval.get(i).getF1EA()))
			  f1+=0;
		  else
                	f1+=qeval.get(i).getF1EA();
                k++;
            }
        }
        
	if(k==0)
	    return 0;
        return f1/k;
    }
   
    /** Phase A Measures **/
    
    /**
     * Calculate MAP for concepts
     * @param qeval     Object with question-level evaluation measures
     * @return          MAP for concepts
     */ 
    public double MapConcepts(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).hasQuestionConcepts()){
	   if(Double.isNaN(qeval.get(i).getAveragePrecisionConcepts()))
		   m+=0;
	   else
                        m+=qeval.get(i).getAveragePrecisionConcepts();
                sz++;
            }
        }
       if(sz==0)
	      return 0; 
        return m/sz;
    }
    /**
     * Calculate MeanPrecision for concepts
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanPrecision for concepts
     */ 
    public double MeanPrecisionConcepts(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).hasQuestionConcepts()){
	   if(Double.isNaN(qeval.get(i).getConceptsPrecision()))
		   m+=0;
	   else
           m+=qeval.get(i).getConceptsPrecision();
           sz++;
            }
        }
       if(sz==0)
	      return 0; 
        return m/sz;
    }
    /**
     * Calculate MeanRecall for concepts
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for concepts
     */ 
    public double MeanRecallConcepts(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).hasQuestionConcepts()){
	   if(Double.isNaN(qeval.get(i).getConceptsRecall()))
		   m+=0;
	   else
                            m+=qeval.get(i).getConceptsRecall();
           sz++;
            }
        }
       if(sz==0)
	   return 0; 
       
        return m/sz;
    }
    /**
     * Calculate MeanF1 for concepts
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for concepts
     */ 
    public double MeanF1Concepts(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).hasQuestionConcepts()){
		 if(Double.isNaN(qeval.get(i).getConceptsF1()))
			      m+=0;
	         else
	           m+=qeval.get(i).getConceptsF1();
           sz++;
            }
        }
        if(sz==0)
		return 0;
        return m/sz;
    }
    /**
     * Calculate MeanPrecision for articles
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanPrecision for articles
     */ 
    public double MeanPrecisionArticles(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        for(int i=0;i<qeval.size();i++)
        {
	   if(Double.isNaN(qeval.get(i).getArticlesPrecision()))
		   m+=0;
	   else
                                m+=qeval.get(i).getArticlesPrecision();
           sz++;
        }
         if(sz==0)
		return 0;
        return m/sz;
    }
    /**
     * Calculate MeanRecall for articles
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for articles
     */ 
    public double MeanRecallArticles(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        for(int i=0;i<qeval.size();i++)
        {
	   if(Double.isNaN(qeval.get(i).getArticlesRecall()))
		   m+=0;
	   else
                                    m+=qeval.get(i).getArticlesRecall();
           sz++;
        }
        if(sz==0)
            return 0;
        return m/qeval.size();
    }
    /**
     * Calculate MeanF1 for articles
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanF1 for articles
     */ 
    public double MeanF1Articles(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
		if(Double.isNaN(qeval.get(i).getArticlesF1()))
			m+=0;
		else
           		m+=qeval.get(i).getArticlesF1();
        }
        
        return m/qeval.size();
    }
    /**
     * Calculate MeanPrecision for Snippets
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanPrecision for Snippets
     */ 
    public double MeanPrecisionSnippets(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
	   if(Double.isNaN(qeval.get(i).getSnippetsPrecision())){
		   m+=0;System.out.println("isnan");}
	   else
           m+=qeval.get(i).getSnippetsPrecision();
        }
        
        return m/qeval.size();
    }
    /**
     * Calculate MeanRecall for Snippets
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for Snippets
     */ 
    public double MeanRecallSnippets(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
	   if(Double.isNaN(qeval.get(i).getSnippetsRecall())){
		   m+=0;System.out.println("isnan");
           }
	   else
           m+=qeval.get(i).getSnippetsRecall();
        }
        
        return m/qeval.size();
    }
    /**
     * Calculate MeanF1 for Snippets
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanF1 for Snippets
     */ 
    public double MeanF1Snippets(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
		if(Double.isNaN(qeval.get(i).getSnippetsF1()))
			m+=0;
		else
           		m+=qeval.get(i).getSnippetsF1();
        }
        
        return m/qeval.size();
    }
    /**
     * Calculate MeanPrecision for Triples
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanPrecision for Triples
     */ 
    public double MeanPrecisionTriples(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int num=0;
        for(int i=0;i<qeval.size();i++)
        {
             if(qeval.get(i).is_triple){
	   if(Double.isNaN(qeval.get(i).getTriplesPrecision()))
		   m+=0;
	   else
                m+=qeval.get(i).getTriplesPrecision();
                num++;
             }
        }
        
        if(num==0)
            return 0;
        
        return m/(double)num;
    }
    /**
     * Calculate MeanRecall for Triples
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for Triples
     */ 
    public double MeanRecallTriples(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int num=0;
        for(int i=0;i<qeval.size();i++)
        {
             if(qeval.get(i).is_triple){
	   if(Double.isNaN(qeval.get(i).getTriplesRecall()))
		   m+=0;
	   else
                m+=qeval.get(i).getTriplesRecall();
                num++;
             }
        }
           if(num==0)
            return 0;
        return m/(double)num;
    }
    /**
     * Calculate MeanF1 for Triples
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanF1 for Triples
     */ 
    public double MeanF1Triples(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int num=0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).is_triple){
		    if(Double.isNaN(qeval.get(i).getTriplesF1()))
			    m+=0;
		    else
                m+=qeval.get(i).getTriplesF1();
                num++;
            }
        }
           if(num==0)
            return 0;
        return m/(double)num;
    }
    /**
     * Calculate Map for Documents
     * @param qeval     Object with question-level evaluation measures
     * @return          Map for Documents
     */ 
    public double MapDocuments(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {
	   if(Double.isNaN(qeval.get(i).getAveragePrecisionDocuments()))
		   m+=0;
	   else
            m+=qeval.get(i).getAveragePrecisionDocuments();
        }
        
        return m/qeval.size();
    }
    /**
     * Calculate Map for Triples
     * @param qeval     Object with question-level evaluation measures
     * @return          Map for Triples
     */ 
    public double MapTriples(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int num = 0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).is_triple){
	   if(Double.isNaN(qeval.get(i).getAveragePrecisionTriples()))
		   m+=0;
	   else
            m+=qeval.get(i).getAveragePrecisionTriples();
            num++;
            }
        }
        if(num==0)
            return 0;
        return m/num;
    }
    /**
     * Calculate MeanRecall for Snippets
     * @param qeval     Object with question-level evaluation measures
     * @return          MeanRecall for Snippets
     */ 
    public double MapSnippets(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {

	   if(Double.isNaN(qeval.get(i).getAveragePrecisionSnippets()))
		   m+=0;
	   else
                                    m+=qeval.get(i).getAveragePrecisionSnippets();
        }
       
        return m/qeval.size();
    }
    /**
     * Calculate GMap for Concepts
     * @param qeval     Object with question-level evaluation measures
     * @return          GMap for Concepts
     */ 
    public double GMapConcepts(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int sz=0;
        
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).hasQuestionConcepts())
            {
	   if(Double.isNaN(qeval.get(i).getAveragePrecisionConcepts()))
		   m+=0;
	   else
                            m+=Math.log(qeval.get(i).getAveragePrecisionConcepts() + epsilon);
                sz++;
            }
        }
    
    if(sz==0)
    return 0;	    
        return Math.exp(m/sz);
    }
    /**
     * Calculate GMap for Documents
     * @param qeval     Object with question-level evaluation measures
     * @return          GMap for Documents
     */ 
    public double GMapDocuments(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m = 0;
        double k=0;
        for (int i = 0; i < qeval.size(); i++) {
            /*if(qeval.get(i).getAveragePrecisionDocuments()==0.0)
            {
            System.out.println(qeval.get(i).getQuestionID());	
            }
            System.out.println(qeval.get(i).getAveragePrecisionDocuments());*/

            if (Double.isNaN(qeval.get(i).getAveragePrecisionDocuments())) {
                m += Math.log(epsilon);
            } else {
                m += Math.log(qeval.get(i).getAveragePrecisionDocuments() + epsilon);
            }
        }
        
        return Math.exp(m/qeval.size());
    }
    /**
     * Calculate GMap for Triples
     * @param qeval     Object with question-level evaluation measures
     * @return          GMap for Triples
     */ 
    public double GMapTriples(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        int num = 0;
        for(int i=0;i<qeval.size();i++)
        {
            if(qeval.get(i).is_triple){
            m+=Math.log(qeval.get(i).getAveragePrecisionTriples()+epsilon);
            num++;
            }
        }
           if(num==0)
            return 0;
        return Math.exp(m/num);
    }
    /**
     * Calculate GMap for Snippets
     * @param qeval     Object with question-level evaluation measures
     * @return          GMap for Snippets
     */ 
    public double GMapSnippets(ArrayList<QuestionAnswerEvaluator> qeval)
    {
        double m=0;
        for(int i=0;i<qeval.size();i++)
        {

	   if(Double.isNaN(qeval.get(i).getAveragePrecisionSnippets()))
		   m+=0;
	   else
            m+=Math.log(qeval.get(i).getAveragePrecisionSnippets()+epsilon);
        }
        
        if(Double.isNaN(m))
            return 0;
        
        if(m==0)
            return 0;
        
        return Math.exp(m/qeval.size());
    }

    /**
     * Options recognized for calling this script
     * @return Options initialized object
     */
    private static Options createOptions()
    {
        Options opt = new Options();
        
        opt.addOption("e", true, "edition of BioASA challenge");
        opt.addOption("phaseA",false,"phase A of Task B");
        opt.addOption("phaseB",false,"phase B of Task B");
        opt.addOption("verbose",false,"verbose output");
        
        return opt;
    }
    /**
     * Set the version of challenge
     * @param VERSION_OF_CHALLENGE 
     */
    private void setVERSION_OF_CHALLENGE(int VERSION_OF_CHALLENGE) {
        this.VERSION_OF_CHALLENGE = VERSION_OF_CHALLENGE;
    }
    /**
     * Set verbosity parameter
     * @param verbosity 
     */
    public void setVerbosity(boolean verbosity) {
        this.verbosity = verbosity;
    }
    /**
     * Describe parameters for calling the evaluation script
     */
    private static void usage()
    {
        System.out.println("Usage: -phaseX [-e version] goldenfile systemfile [-verbose]");
        System.out.println("Where X can be either A or B for the corresponding phases,");
        System.out.println("goldenfile systemfile are the files (golden and submitted respectively) ");
        System.out.println("and version of the challenge can be 2 (for BioASQ1&2), 3 (for BioASQ3&4), 5 (for BioASQ5,6&7) or 8 (for BioASQ8 and later). "
                + "This argument is optional - default value is 2)");
        System.out.println("verbose, also optional, enables human readable output.");
    }
    /**
     * Handle initial call of evaluation script, taking into account the parameters given.
     * @param args 
     */
    public static void main(String args[])
    {
        Options opt = EvaluatorTask1b.createOptions();

        CommandLineParser parser = new  PosixParser();
        
        try {
            CommandLine line = parser.parse(opt, args);
            String e;
            EvaluatorTask1b eval;
            
            if (!line.hasOption("phaseA") && !line.hasOption("phaseB")) {
                EvaluatorTask1b.usage();
                System.exit(0);
            }
            
            if (line.hasOption("e")) {
                e = line.getOptionValue("e");
                if (e == null) {
                    EvaluatorTask1b.usage();
                    System.exit(0);
                }
            
                eval = new EvaluatorTask1b(args[3], args[4],Integer.parseInt(e));
            } else {
                eval = new EvaluatorTask1b(args[1], args[2],EvaluatorTask1b.BIOASQ2);
            }

            if(line.hasOption("verbose")){
                eval.setVerbosity(true);
            }
            if (line.hasOption("phaseA")) {
                eval.EvaluatePhaseA();
            }
            if (line.hasOption("phaseB")) {
                eval.EvaluatePhaseB();
            }

        } catch (ParseException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

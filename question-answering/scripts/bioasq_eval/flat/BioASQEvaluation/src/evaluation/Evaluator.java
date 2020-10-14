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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
/** This script called for BioASQ Task A evaluation, for flat measures.
 * 
 *  Example call
 *      java -cp BioASQEvaluation2018.jar evaluation.Evaluator "...\golden_labels.txt" "...\submission_mapped.txt" -verbose
 *      or java -cp BioASQEvaluation2018.jar evaluation.Evaluator "...\golden_labels.txt" "...\submission_mapped.txt" -verbose
 *          *golden_labels.txt and submission_mapped.txt should have been mapped to integer format using converters.MapMeshResults
 * @author tasosnent
 */
public class Evaluator {
    Map class_results;
    ArrayList<String> truePredictions;
    int size_of_true_labels;
    int size_of_predicted_label;
    private boolean verbosity = false;

    public Evaluator()
    {
        class_results = new TreeMap<Integer,ConfusionMatrix>();
        size_of_true_labels = 0;
    }

    public Evaluator(ArrayList<Integer> class_ids)
    {
        class_results = new TreeMap<Integer,ConfusionMatrix>();
        for(int i=0;i<class_ids.size();i++)
        {
            class_results.put(new Integer(class_ids.get(i)),new ConfusionMatrix());
        }
    }
    
    public void increaseTP(int class_id)
    {
        ConfusionMatrix cm = (ConfusionMatrix)class_results.get(class_id);
        try{
            cm.increaseTP();
         }catch(NullPointerException ex){
            //System.out.println("Class id: "+class_id);
            class_results.put(new Integer(class_id), new ConfusionMatrix());
            cm = (ConfusionMatrix)class_results.get(class_id);
            cm.increaseTP();
        }
    }
    
    public void increaseTN(int class_id)
    {
        ConfusionMatrix cm = (ConfusionMatrix)class_results.get(class_id);
        
        cm.increaseTN();
    }
     
    public void increaseFP(int class_id)
    {
        ConfusionMatrix cm = (ConfusionMatrix)class_results.get(class_id);
        
        try{
        cm.increaseFP();
        }catch(NullPointerException ex){
            class_results.put(new Integer(class_id), new ConfusionMatrix());
            cm = (ConfusionMatrix)class_results.get(class_id);
            cm.increaseFP();
        }
    }
    
    public void increaseFN(int class_id)
    {
        ConfusionMatrix cm = (ConfusionMatrix)class_results.get(class_id);
         try{
        cm.increaseFN();
         }catch(NullPointerException ex){
            class_results.put(new Integer(class_id), new ConfusionMatrix());
            cm = (ConfusionMatrix)class_results.get(class_id);
            cm.increaseFN();
        }
    }
   
    /**
    * Calculates the Micro-precision measure for multilabel cases.
    *
     * @return 
    */ 
    public double microPrecision()
    {
        int a=0,b=0;
        Iterator iterator = class_results.keySet().iterator();
        
        while(iterator.hasNext())
        {
            Integer cl = (Integer)iterator.next();
            ConfusionMatrix cm = (ConfusionMatrix)class_results.get(cl);
            a+= cm.getTp();
            b+= cm.getTp()+cm.getFp();
        }
        
        return (double)a/(double)b;
    }
    
    public double microRecall()
    {
        int a=0,b=0;
        Iterator iterator = class_results.keySet().iterator();
        
        while(iterator.hasNext())
        {
            Integer cl = (Integer)iterator.next();
            ConfusionMatrix cm = (ConfusionMatrix)class_results.get(cl);
            a+= cm.getTp();
            b+= cm.getTp()+cm.getFn();
        }
        
        return (double)a/(double)b;
    }

    public double microFmeasure()
    {
        double a = microPrecision();
        double b = microRecall();
        
        return 2*a*b/(a+b);
    }
    
    public double macroPrecision()
    {
        int a=0,b=0;
        Iterator iterator = class_results.keySet().iterator();
        double sum=0.0;
        
        while(iterator.hasNext())
        {
            Integer cl = (Integer)iterator.next();
            ConfusionMatrix cm = (ConfusionMatrix)class_results.get(cl);
            if(cm.getTp()==0 && cm.getFp()==0)
                continue;
            
             sum+= (double)cm.getTp()/(double)(cm.getTp()+cm.getFp());
           
        }
        
        return sum/(double)this.size_of_predicted_label;
    }
    
    public double macroRecall()
    {
        double sum=0.0;
        Iterator iterator = class_results.keySet().iterator();
        
        while(iterator.hasNext())
        {
            Integer cl = (Integer)iterator.next();
            ConfusionMatrix cm = (ConfusionMatrix)class_results.get(cl);
            if(cm.getTp()==0 && cm.getFn()==0)
                continue;
            
                sum+= (double)cm.getTp()/(double)(cm.getTp()+cm.getFn());
        }
        
         return sum/(double)this.size_of_true_labels;
    }

    public double macroFmeasure()
    {
        Iterator iterator = class_results.keySet().iterator();
        
        double pre=0.0;
        double rec=0.0;
        double macroF=0.0;
        
        while(iterator.hasNext())
        {
            Integer cl = (Integer)iterator.next();
            ConfusionMatrix cm = (ConfusionMatrix)class_results.get(cl);
            if(cm.getTp()!=0 || cm.getFp()!=0)
            {           
              pre = (double)cm.getTp()/(double)(cm.getTp()+cm.getFp());
            }
         
            if(cm.getTp()!=0 || cm.getFn()!=0)
            {
              rec= (double)cm.getTp()/(double)(cm.getTp()+cm.getFn());
            }
            
            if(pre!=0 || rec!=0)
                macroF += (2*pre*rec)/(pre+rec);
        }
        
        return macroF/this.size_of_true_labels;
    }
    
    /**
     * This function loads from a file the true labels.
     *
     * @param trueLabels    the full path to the file with the true labels
     */ 
    public void loadTrueLabels(String trueLabels){
            BufferedReader br2 = null;
            truePredictions = new ArrayList<String>();
            int row = 0;
            try {

                br2 = new BufferedReader(new FileReader(trueLabels));

                String true_preds;
                while((true_preds=br2.readLine())!=null){
                    row++;

                   truePredictions.add(true_preds);
                   String []true_labels = true_preds.split("\\s+");
                   for(int i=0;i<true_labels.length;i++)
                   {
                       Integer intLabel = Integer.parseInt(true_labels[i]);
                       if(!class_results.containsKey(intLabel))
                       {
                            class_results.put(intLabel,new ConfusionMatrix());
                       }
                   }

              }

                size_of_true_labels = class_results.size();

            } catch (IOException ex) {

                System.out.println("File not found: "+trueLabels + " or unable to read file");
                System.out.println(ex.getMessage());
            }finally{
                try{
                    if (br2!=null){

                        br2.close();
                    }

                }catch(IOException ex){
                    System.out.println(ex);
                }
            }
        }

    /**
     * This function removes duplicates from an array of given labels. It is used while
     * reading the file with the predicted labels.
     *
     * @param labels    the array with the labels to be checked for duplicates
     */ 
    public String[] removeDuplicates(String labels[])
    {
        TreeSet aset = new TreeSet();
          aset.addAll(Arrays.asList(labels));

          int num_of_labels = aset.size();

          String finallabels[] = new String[num_of_labels];
          Iterator iterator = aset.iterator();
          int k=0;
          while(iterator.hasNext())
          {
              finallabels[k++] = (String)iterator.next();
          }

          return finallabels;
    }

    public void evaluateTLExternal(String resultsFile)
    {
         BufferedReader br=null;

         double accuracy=0.0;
         double example_based_precision=0.0;
         double example_based_recall=0.0;
         double example_based_f = 0.0;

         HashSet<Integer> labels_in_predictions = new HashSet<Integer>();

         int row = 0;

         try {
            br = new BufferedReader(new FileReader(resultsFile));

            String line;
            while((line=br.readLine())!=null){

                String predicted_values[] = line.split("\\s+");
                predicted_values = removeDuplicates(predicted_values);

                String tpres = (String)truePredictions.get(row);
                String true_labels[] = tpres.split("\\s+");

                double common_labels=0;

                for(int k=0;k<true_labels.length;k++) // find the common labels
                {
                  Integer trueLab = Integer.parseInt(true_labels[k]);

                  boolean foundLabel=false;
                  for(int j=0;j<predicted_values.length;j++)
                  {
                      Integer predLab = Integer.parseInt(predicted_values[j]);
                      if(predLab.intValue()==trueLab.intValue())
                      {
                          common_labels+=1.0;
                          foundLabel = true;
                          break;
                      }
                  }
                  if(!foundLabel) // this is for label based measures
                      increaseFN(trueLab);
                }

                // calculate label based measures

                for(int j=0;j<predicted_values.length;j++)
                {
                    Integer predLab = Integer.parseInt(predicted_values[j]);
                    labels_in_predictions.add(predLab);

                    boolean foundLabel=false;
                    for(int k=0;k<true_labels.length;k++)
                    {
                        Integer trueLab = Integer.parseInt(true_labels[k]);
                        if(trueLab.intValue()==predLab.intValue())
                        {
                            increaseTP(trueLab);
                            foundLabel = true;
                            break;
                        }
                    }
                    if(!foundLabel)
                    {
                        increaseFP(predLab);
                    }
                }

                accuracy+=common_labels/(double)(allLabels(true_labels,predicted_values));

                example_based_precision += common_labels/(double)predicted_values.length;
                example_based_recall += common_labels/(double)true_labels.length;
                example_based_f += (2*common_labels/(double)(true_labels.length+predicted_values.length));

                row++;
          } // for each test instance

            size_of_predicted_label = labels_in_predictions.size();

            String output="";
            output+= accuracy/(double)row+" ";
            output+= example_based_precision/(double)row+" ";
            output+= example_based_recall/(double)row +" ";
            output+= example_based_f/(double)row+" ";
            output+= macroPrecision()+" ";
            output+=macroRecall()+" ";
            output+=macroFmeasure()+" ";
            output+=microPrecision()+" ";
            output+=microRecall()+" ";
            output+=microFmeasure();

            System.out.print(output);

            if(this.verbosity){
                System.out.println("\nAccuracy: "+accuracy/(double)row);
                System.out.println("EBP :"+example_based_precision/(double)row);
                System.out.println("EBR :"+example_based_recall/(double)row);
                System.out.println("EBF :"+example_based_f/(double)row);

                System.out.println("MaP :"+macroPrecision());
                System.out.println("MaR :"+macroRecall());
                System.out.println("MaF :"+macroFmeasure());

                System.out.println("MiP :"+microPrecision());
                System.out.println("MiR :"+microRecall());
                System.out.println("MiF :"+microFmeasure());
            }
        } catch (IOException ex) {

            System.out.println("File not found: "+resultsFile + " or unable to read file");
            System.out.println(ex.getMessage());
        }catch(NumberFormatException exn){
        System.out.println(exn);
        System.out.println("Line: "+ row);
        }finally{
            try{
                if (br!=null){
                    br.close();
                }

            }catch(IOException ex){
                System.out.println(ex);
            }
        }
    }

    int allLabels(String list1[],String list2[])
    {
        HashSet<Integer> labels_per_instance = new HashSet<Integer>();

        for(int i=0;i<list1.length;i++)
            labels_per_instance.add(new Integer(Integer.parseInt(list1[i])));
        for(int i=0;i<list2.length;i++)
            labels_per_instance.add(new Integer(Integer.parseInt(list2[i])));

        return labels_per_instance.size();
    }

    /**
     * Describe parameters for calling the evaluation script
     */
    private static void usage()
    {
        System.out.println("Usage: "+Evaluator.class.getName()+" goldendata systemanswers [-verbose]");
        System.out.println("goldendata systemanswers are the files (golden and submitted respectively)");
        System.out.println("verbose (optional) enables human readable output.");
    }
    
    public static void main(String args[])
    {

       // The main function to perform the evaluation of a multi-label classification task. 
       // args[0] holds the file with the true labels
       // args[1] holds the file with the system's labels
       // The format of the two files is as following:
       //
       // 145 4567 22213
       // 234 5321 3456
       // 123
       // 123 125
       // etc.
       //
       //
       // Each line holds the labels for the corresponding instance seperated by a space

        Options opt = new Options();
        opt.addOption("verbose",false,"verbose output");

        CommandLineParser parser = new  PosixParser();
        
        try {
            CommandLine line = parser.parse(opt, args);
            if(args.length<2)
            {
                usage();
                System.exit(0);
            }
            Evaluator eval = new Evaluator();
            if(line.hasOption("verbose")){
                eval.setVerbosity(true);
            }
            eval.loadTrueLabels(args[0]);
            eval.evaluateTLExternal(args[1]);
            
        } catch (ParseException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the verbosity
     */
    public boolean isVerbosity() {
        return verbosity;
    }

    /**
     * @param verbosity the verbosity to set
     */
    public void setVerbosity(boolean verbosity) {
        this.verbosity = verbosity;
    }
}

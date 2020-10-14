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

public class ExactAnswer {
    String answer; // case of yesno questions
    ArrayList<String> answers; // case of factoids and lists
    ArrayList<ArrayList<String>> lists;
    
    public ExactAnswer()
    {
        answers = new ArrayList<String>();
        lists = new ArrayList<ArrayList<String>>();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public ArrayList<ArrayList<String>> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ArrayList<String>> lists) {
        this.lists = lists;
    }
    
    boolean containsAnswer(String resp)
    {
        for(int i=0;i<answers.size();i++)
            if(answers.get(i).equals(resp))
                return true;
        
        return false;
    }
    
    public   boolean containsAnswerSynonym(ArrayList<String> resp,boolean remove)
    {
        for(int i=0;i<lists.size();i++)
        {
            ArrayList<String> listofans = lists.get(i);
            for(int k=0;k<listofans.size();k++)
            {
                String ans1 = listofans.get(k);
                for(int l=0;l<resp.size();l++)
                {
                    if(ans1.equals(resp.get(l)))
                    {
                       // System.out.println(ans1+" "+resp.get(l));
                        if(remove) // will use this only for list questions
                            lists.remove(i);
                        return true;
                    }
                }
            }
        }
           
        return false;
    }

    
}

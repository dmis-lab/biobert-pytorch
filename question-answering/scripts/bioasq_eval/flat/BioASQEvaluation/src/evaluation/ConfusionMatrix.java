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

/**
 * This class implements a confusion matrix between
 * two objects containing list of items 
 * (for example classes in a classification problem for documents).
 * 
 *
 */
public class ConfusionMatrix {
    int tp; // count for true positives
    int tn; // count for true negatives
    int fp; // count for false positives
    int fn; // count for false negatives
    
    public ConfusionMatrix()
    {
        tp=0;fp=0;tn=0;fn=0;
    }
    
    public void increaseTP()
    {
        tp++;
    }
    
    public void increaseTN()
    {
        tn++;
    }
    
    public void increaseFP()
    {
        fp++;
    }
     
    public void increaseFN()
    {
        fn++;
    }

    public int getFn() {
        return fn;
    }

    public int getFp() {
        return fp;
    }

    public int getTn() {
        return tn;
    }

    public int getTp() {
        return tp;
    }
}

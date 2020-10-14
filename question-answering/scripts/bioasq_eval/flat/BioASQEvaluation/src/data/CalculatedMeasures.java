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

public class CalculatedMeasures {
    double precision;
    double recall;
    double fmeasure;
    double average_precision;
    
    double accuracy; // for phase B - exact answers for yesno questions
    double strict_accuracy;
    double lenient_accuracy;
    double mean_reciprocal_rank;

    public CalculatedMeasures() {
        precision = 0;
        recall=0;
        fmeasure=0;
        average_precision=0;
        
        accuracy=0;
        strict_accuracy = 0;
        lenient_accuracy = 0;
        mean_reciprocal_rank = 0;
                
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getLenient_accuracy() {
        return lenient_accuracy;
    }

    public void setLenient_accuracy(double lenient_accuracy) {
        this.lenient_accuracy = lenient_accuracy;
    }

    public double getMean_reciprocal_rank() {
        return mean_reciprocal_rank;
    }

    public void setMean_reciprocal_rank(double mean_reciprocal_rank) {
        this.mean_reciprocal_rank = mean_reciprocal_rank;
    }

    public double getStrict_accuracy() {
        return strict_accuracy;
    }

    public void setStrict_accuracy(double strict_accuracy) {
        this.strict_accuracy = strict_accuracy;
    }

    public double getAverage_precision() {
        return average_precision;
    }

    public void setAverage_precision(double average_precision) {
        this.average_precision = average_precision;
    }

    public double getFmeasure() {
        return fmeasure;
    }

    public void setFmeasure(double fmeasure) {
        this.fmeasure = fmeasure;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }
    
    
}

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

public class Triple {
    String predicate;
    String subject;
    String operator;

    public Triple(String predicate, String subject, String operator) {
        this.predicate = predicate;
        this.subject = subject;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.predicate != null ? this.predicate.hashCode() : 0);
        hash = 47 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 47 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        return hash;
    }
    
    @Override public boolean equals(Object e)
    {
        if ( this == e ) return true;
        
        if ( !(e instanceof Triple) ) return false;
        
        Triple tr = (Triple)e;
        
        return this.predicate.equals(tr.predicate) && this.operator.equals(tr.operator) &&
                this.subject.equals(tr.subject);
    }
}

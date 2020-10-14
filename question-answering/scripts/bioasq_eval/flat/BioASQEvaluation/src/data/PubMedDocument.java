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

public class PubMedDocument {
    String text;
    String title;
    String pmid;
    String journal;
    String[] meshMajor;

    public PubMedDocument(String text, String title, String pmid, String journal, String[] meshMajor) {
        this.text = text;
        this.title = title;
        this.pmid = pmid;
        this.journal = journal;
        this.meshMajor = meshMajor;
    }

    public String getJournal() {
        return journal;
    }

    public String[] getMeshMajor() {
        return meshMajor;
    }

    public String getPmid() {
        return pmid;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
    
    
    
}

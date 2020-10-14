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

public class Snippet {
    String document;
    String text;
    String fieldNameBegin;
    String fieldNameEnd;
    int begin_index;
    int end_index;

    public Snippet(String document, String text, String fieldNameBegin, String fieldNameEnd, int begin_index, int end_index) {
        this.document = document;
        this.text = text;
        this.fieldNameBegin = fieldNameBegin;
        this.fieldNameEnd = fieldNameEnd;
        this.begin_index = begin_index;
        this.end_index = end_index;
    }

    public void setBegin_index(int begin_index) {
        this.begin_index = begin_index;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setEnd_index(int end_index) {
        this.end_index = end_index;
    }

    public void setFieldNameBegin(String fieldNameBegin) {
        this.fieldNameBegin = fieldNameBegin;
    }

    public void setFieldNameEnd(String fieldNameEnd) {
        this.fieldNameEnd = fieldNameEnd;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBegin_index() {
        return begin_index;
    }

    public String getDocument() {
        return document;
    }

    public String getDocumentOnlyID() {
        String parts[] = document.split("/");
        //System.out.println(parts[parts.length-1]);
        return parts[parts.length-1];
    }
    
    public int getEnd_index() {
        return end_index;
    }

    public String getFieldNameBegin() {
        return fieldNameBegin;
    }

    public String getFieldNameEnd() {
        return fieldNameEnd;
    }

    public String getText() {
        return text;
    }
    
    public int  getSize()
    {
        return end_index-begin_index+1;
    }
    
    public double overlap(Snippet sn)
    {
        
        if((fieldNameBegin.equals(sn.getFieldNameBegin()) && fieldNameEnd.equals(sn.getFieldNameEnd()))||
                (fieldNameBegin.equals("0") && sn.getFieldNameBegin().equals("abstract") ))
        {
            if(begin_index>sn.getEnd_index() || end_index<sn.getBegin_index())
                return 0;
            else
            {
                if(begin_index>=sn.getBegin_index() && end_index<=sn.getEnd_index())
                    return end_index-begin_index+1;
                if(begin_index>=sn.getBegin_index() && end_index>sn.getEnd_index())
                    return sn.getEnd_index() - begin_index +1;
                if(begin_index<sn.getBegin_index() && end_index<=sn.getEnd_index())
                    return end_index - sn.getBegin_index() +1;
                if(begin_index<sn.getBegin_index() && end_index>sn.getEnd_index())
                    return sn.getEnd_index()-sn.getBegin_index()+1;
            }
        }
       
        return 0;
        
    }
    
    public boolean itOverlaps(Snippet sn)
    {
           if((fieldNameBegin.equals(sn.getFieldNameBegin()) && fieldNameEnd.equals(sn.getFieldNameEnd()))||
                (fieldNameBegin.equals("0") && sn.getFieldNameBegin().equals("abstract") ))
        {
            if(begin_index>sn.getEnd_index() || end_index<sn.getBegin_index())
                return false;
            else
            {
                if(begin_index>=sn.getBegin_index() && end_index<=sn.getEnd_index())
                    return true;
                if(begin_index>=sn.getBegin_index() && end_index>sn.getEnd_index())
                    return true;
                if(begin_index<sn.getBegin_index() && end_index<=sn.getEnd_index())
                    return true;
                if(begin_index<sn.getBegin_index() && end_index>sn.getEnd_index())
                    return true;
            }
        }
       
        return false;
    }
    
    public Snippet merge(Snippet sn)
    {
        Snippet newsn = new Snippet(document, text, fieldNameBegin, fieldNameEnd, -1, -1);
        
        if(begin_index<=sn.begin_index)
            newsn.setBegin_index(begin_index);
        else
            newsn.setBegin_index(sn.begin_index);
        
        if(end_index>=sn.end_index)
            newsn.setEnd_index(end_index);
        else
            newsn.setEnd_index(sn.end_index);
        
        return newsn;
    }

}

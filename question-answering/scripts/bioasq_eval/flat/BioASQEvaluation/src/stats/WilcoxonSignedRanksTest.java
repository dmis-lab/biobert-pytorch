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
package stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WilcoxonSignedRanksTest {
    
    private double MIN=-1.0;
    
    public WilcoxonSignedRanksTest()
    {
        
    }
    
    public void test()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
       String s;
       int i=0;
       double values1[] = new double[6];
       double values2[] = new double[6];
       
        try {
            while ((s = in.readLine()) != null && s.length() != 0){
               String values[] = s.split("\\s+");
               
               
               values1[i] = Double.parseDouble(values[0]);
               values2[i] = Double.parseDouble(values[1]);
               
               
               
               i++;
              //System.out.println(s);
            }
            
            performTest(values1, values2);
        } catch (IOException ex) {
            Logger.getLogger(WilcoxonSignedRanksTest.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
    public int [] nextMinIndexes(double []acc,double min)
    {
	int [] ids=new int[acc.length];
	int j=0;
	double m=1.0;

	for(int i=0;i<acc.length;i++)
	    acc[i] = Math.abs(acc[i]);
	// Find the min

	for(int i=0;i<acc.length;i++)
	    if(acc[i]>min && m>acc[i])
		m = acc[i];

	for(int i=0;i<acc.length;i++)
	    if(Math.abs(acc[i])==m)
	    {
		ids[j]=i;
		j++;
	    }

	int [] rids = new int[j];
	for(int i=0;i<j;i++)
	{
	    rids[i] = ids[i];
	}
	MIN = acc[rids[0]];
	//System.exit(0);

	return rids;
    }


    public double [] performTest(double [] m1, double [] m2)
    {
	int l = m1.length;
	int l2 = m2.length;

	if(l != l2)
	{
	    System.out.println("The tables are not equalsized");
	    System.exit(0);
	}

	int max_rank = l;
	int rank=1;
	int [] ids = new int[l];

	double mean_rank;
	double [] dif = new double[l];
	double [] ranks = new double[l];

	for(int i=0;i<l;i++)
	    dif[i] = m1[i]-m2[i];

//	System.out.println(Arrays.toString(dif));

	while(rank<=max_rank)
	{
	    ids = nextMinIndexes(dif.clone(),MIN);

	    mean_rank = 0.0;

	    for(int j=0;j<ids.length;j++)
	    {
		mean_rank = mean_rank + rank;
		rank+=1;
	    }
	    mean_rank/=ids.length;

	    for(int j=0;j<ids.length;j++)
	    {
		mean_rank = Math.round(mean_rank*1000)/1000.0;
		ranks[ids[j]]=mean_rank;
	    }

	}

	double rminus=0.0;
	double rpos = 0.0;
	double rzero = 0.0;
	double rankzero = 0;
	int countzeros=0;

	for(int i=0;i<l;i++)
	{
	    if(dif[i]<0.0)
		rminus+=ranks[i];
	    else if(dif[i]>0.0)
		rpos+=ranks[i];
	    else
	    {
		rankzero = ranks[i];
		rzero += ranks[i];
		countzeros+=1;
	    }
	}

	if(countzeros%2!=0)
	    rzero-=rankzero;
	
	rpos+=rzero/2.0;
	rminus+=rzero/2.0;

	System.out.println("R+: "+rpos+" R-: "+rminus);
	System.out.println("T = "+Math.min(rpos,rminus));

	return ranks;
    }
    
    public static void main(String args[])
    {
        WilcoxonSignedRanksTest stest = new WilcoxonSignedRanksTest();
        stest.test();
    }
}

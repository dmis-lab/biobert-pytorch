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

package tools;

import java.util.ArrayList;

public class MathUtils{

    public static double SMALL = 1e-6;
  
    private MathUtils(){};

    public static double average(double values[])
    {
        int s = values.length;
        if(s==0)
            return Double.MIN_VALUE;
        double avg = 0.0;
        for(int i=0;i<s;i++)
            avg+=values[i];

        return avg/s;
    }

    public static double max( double[] arr ) {
        double max = -Double.MAX_VALUE;
        int length = arr.length;

        for (int i=0; i < length; ++i)
                if (arr[i] > max)
                        max = arr[i];
        return max;
    }

    public static double min( double[] arr ) {
        double min = Double.MAX_VALUE;
        int length = arr.length;

        for (int i=0; i < length; ++i)
                if (arr[i] < min)
                        min = arr[i];

        return min;
    }

    public static int min( int[] arr ) {
        int min = Integer.MAX_VALUE;
        int length = arr.length;

        for (int i=0; i < length; ++i)
                if (arr[i] < min)
                        min = arr[i];
        return min;
    }

    public static int min( ArrayList<Integer> arr ) {
        int min = Integer.MAX_VALUE;
        int length = arr.size();

        for (int i=0; i < length; ++i)
                if (arr.get(i).intValue() < min)
                        min = arr.get(i).intValue();
        return min;
    }

    /**
    *  Returns the range of the data in the specified array.
    *  Range is the difference between the maximum and minimum
    *  values in the data set.
    *
    *  @param  arr  An array of sample data values.
    *  @return The range of the data in the input array.
    **/
    public static double range(double[] arr) {
        return max(arr) - min(arr);
    }

    /**
    *  Returns the root mean square of an array of sample data.
    *
    *  @param   arr  An array of sample data values.
    *  @return  The root mean square of the sample data.
    **/
    public static double rms( double[] arr ) {
        int size = arr.length;
        double sum = 0;
        for (int i=0; i < size; ++i)
                sum += arr[i]*arr[i];

        return Math.sqrt(sum/size);
    }

    public static double variance(double[] arr) {
        int n = arr.length;
        if (n < 2)
                return 0;
                //throw new IllegalArgumentException("Must be at least 2 elements in array.");

        //	1st get the average of the data.
        double ave = average(arr);

        double var = 0;
        double ep = 0;
        for (int i=0; i < n; ++i) {
                double s = arr[i] - ave;
                ep += s;
                var += s*s;
        }

        var = (var - ep*ep/n)/(n-1);

        return var;
    }

     public static double variance(double[] arr,double average) {
        int n = arr.length;
        if (n < 2)
                return 0;
                //throw new IllegalArgumentException("Must be at least 2 elements in array.");

        //	1st get the average of the data.
        double ave = average;

        double var = 0;
        double ep = 0;
        for (int i=0; i < n; ++i) {
                double s = arr[i] - ave;
                ep += s;
                var += s*s;
        }

        var = (var - ep*ep/n)/(n-1);

        return var;
    }

    /**
    *  Returns the standard deviation of an array of sample data.
    *
    *  @param  arr  An array of sample data values.
    *  @return The standard deviation of the sample data.
    **/
    public static double sdev(double[] arr) {
        return Math.sqrt(variance(arr));
    }

    public static double dif(double Dx, double Dy)
    {
        return Dx/Dy;
    }

    public static int indexOf(double[] values,double val)
    {
        for (int i = 0; i < values.length; i++) {
            if(values[i]==val)
                return i;
        }
        return -1;
    }

    public static int indexOf(int[] values,int val)
    {
        for (int i = 0; i < values.length; i++) {
            if(values[i]==val)
                return i;
        }
        return -1;
    }
      
    public static /*@pure@*/ int maxIndex(double[] doubles) {

    double maximum = 0;
    int maxIndex = 0;

    for (int i = 0; i < doubles.length; i++) {
        if ((i == 0) || (doubles[i] > maximum)) {
          maxIndex = i;
          maximum = doubles[i];
        }
    }

    return maxIndex;
  }

    public static /*@pure@*/ int maxIndex(int[] ints) {

    int maximum = 0;
    int maxIndex = 0;

    for (int i = 0; i < ints.length; i++) {
        if ((i == 0) || (ints[i] > maximum)) {
          maxIndex = i;
          maximum = ints[i];
        }
    }

    return maxIndex;
  }
  
    public static double sum(double[] sim_values) {
        double sum=0.0;
        
        for(int i=0;i<sim_values.length;i++)
            sum+=sim_values[i];
        
        return sum;
    }
    
    public static void normalize(double[] doubles, double sum) {

    if (Double.isNaN(sum)) {
      throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
    }
    if (sum == 0) {
      // Maybe this should just be a return.
      throw new IllegalArgumentException("Can't normalize array. Sum is zero.");
    }
    for (int i = 0; i < doubles.length; i++) {
      doubles[i] /= sum;
    }
  }
        
    public static /*@pure@*/ int[] stableSort(double[] array){

    int[] index = new int[array.length];
    int[] newIndex = new int[array.length];
    int[] helpIndex;
    int numEqual;
    
    array = (double[])array.clone();
    for (int i = 0; i < index.length; i++) {
        index[i] = i;
        if (Double.isNaN(array[i])) {
            array[i] = Double.MAX_VALUE;
        }
    }
    quickSort(array,index,0,array.length-1);

    // Make sort stable

    int i = 0;
    while (i < index.length) {
        numEqual = 1;
        for (int j = i+1; ((j < index.length) && eq(array[index[i]],array[index[j]])); j++)
            numEqual++;
        if (numEqual > 1) {
            helpIndex = new int[numEqual];
            for (int j = 0; j < numEqual; j++)
                helpIndex[j] = i+j;
            quickSort(index, helpIndex, 0, numEqual-1);
            for (int j = 0; j < numEqual; j++) 
                newIndex[i+j] = index[helpIndex[j]];
            i += numEqual;
        } else {
            newIndex[i] = index[i];
            i++;
        }
    }

    return newIndex;
  }
      
    public static /*@pure@*/ boolean eq(double a, double b){
    
    return (a - b < SMALL) && (b - a < SMALL); 
  }
        
    /**
    * Implements quicksort according to Manber's "Introduction to
    * Algorithms".
    *
    * @param array the array of integers to be sorted
    * @param index the index into the array of integers
    * @param left the first index of the subset to be sorted
    * @param right the last index of the subset to be sorted
    */
    //@ requires 0 <= first && first <= right && right < array.length;
    //@ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] && index[i] < array.length);
    //@ requires array != index;
    //  assignable index;
    private static void quickSort(/*@non_null@*/ int[] array, /*@non_null@*/  int[] index, int left, int right) {
        if (left < right) {
            int middle = partition(array, index, left, right);
            quickSort(array, index, left, middle);
            quickSort(array, index, middle + 1, right);
        }
  }
    /**
    * Implements quicksort according to Manber's "Introduction to
    * Algorithms".
    *
    * @param array the array of doubles to be sorted
    * @param index the index into the array of doubles
    * @param left the first index of the subset to be sorted
    * @param right the last index of the subset to be sorted
    */
    //@ requires 0 <= first && first <= right && right < array.length;
    //@ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] && index[i] < array.length);
    //@ requires array != index;
    //  assignable index;
    private static void quickSort(/*@non_null@*/ double[] array, /*@non_null@*/ int[] index, int left, int right) {
        if (left < right) {
            int middle = partition(array, index, left, right);
            quickSort(array, index, left, middle);
            quickSort(array, index, middle + 1, right);
        }
    }
  
    /**
   * Partitions the instances around a pivot. Used by quicksort and
   * kthSmallestValue.
   *
   * @param array the array of doubles to be sorted
   * @param index the index into the array of doubles
   * @param l the first index of the subset 
   * @param r the last index of the subset 
   *
   * @return the index of the middle element
   */
    private static int partition(double[] array, int[] index, int l, int r) {
        double pivot = array[index[(l + r) / 2]];
        int help;

        while (l < r) {
            while ((array[index[l]] < pivot) && (l < r)) {
                l++;
            }
            while ((array[index[r]] > pivot) && (l < r)) {
                 r--;
            }
            if (l < r) {
                help = index[l];
                index[l] = index[r];
                index[r] = help;
                l++;
                r--;
            }
        }
        if ((l == r) && (array[index[r]] > pivot)) {
            r--;
        } 

        return r;
  }
  
    private static int partition(int[] array, int[] index, int l, int r) {
    double pivot = array[index[(l + r) / 2]];
    int help;

    while (l < r) {
        while ((array[index[l]] < pivot) && (l < r)) {
            l++;
        }
        while ((array[index[r]] > pivot) && (l < r)) {
            r--;
        }
        if (l < r) {
            help = index[l];
            index[l] = index[r];
            index[r] = help;
            l++;
            r--;
        }
    }
    if ((l == r) && (array[index[r]] > pivot)) {
        r--;
    } 

    return r;
  }
  
    public static /*@pure@*/ int round(double value) {
        int roundedValue = value > 0
          ? (int)(value + 0.5)
          : -(int)(Math.abs(value) + 0.5);

        return roundedValue;
  }
  
}

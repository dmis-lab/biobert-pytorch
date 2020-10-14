/* 
 * File:   tools.h
 * Author: aris
 *
 * Created on November 9, 2011, 4:03 PM
 */

#ifndef TOOLS_H
#define	TOOLS_H
#include <vector>
#include <set>
#include <sstream>
#include <iostream>
using namespace std;

static vector<string> split(string line, char s=' ') {
  vector<string> ret;
  string word = "";
  for(int i=0;i<line.length();i++)
  {
    if (line[i] == s)
    {
      ret.push_back(word);
      word = "";
    }
    else
    {
      word += line[i];
    }
  }
  if (word != "")
    ret.push_back(word);
  return ret;
}

static int stringToInt (string s) {
  stringstream ss;
  ss << s;
  int ret;
  ss >> ret;
  return ret;
}

static string intToString (int i) {
  stringstream ss;
  ss << i;
  string ret;
  ss >> ret;
  return ret;
}
static set<int> fillSet (string line) {
    vector<string> v = split(line);
    set<int> ret;
    for (int i=0;i<v.size();i++)
        ret.insert(stringToInt(v[i]));
    return ret;
}

static set<int> getSubsetMinusCommon (set<int>& target, set<int>& other) {
    set<int>::iterator t_iter, o_iter;
    set<int> ret;
    for (t_iter=target.begin();t_iter!=target.end();t_iter++) {
        int n = *t_iter;
        o_iter =  other.find(n);
        if (o_iter == other.end())
            ret.insert(n);
    }
    return ret;
}
static set<int> addSets (set<int>& s1, set<int>& s2) {
    set<int> ret;
    if (s1.size() > s2.size()) {
        ret = s1;
        set<int>::iterator s_iter;
        for (s_iter=s2.begin();s_iter!=s2.end();s_iter++)
                ret.insert(*s_iter);
    }
    else {
        ret = s2;
        set<int>::iterator s_iter;
        for (s_iter=s1.begin();s_iter!=s1.end();s_iter++)
                ret.insert(*s_iter);
    }
    return ret;
}

static set<int> getIntrOfSets (set<int>& s1, set<int>& s2) {
    set<int> ret;
    set<int>::iterator s_iter1,s_iter2;
    for (s_iter1=s1.begin();s_iter1!=s1.end();s_iter1++) {
        s_iter2 = s2.find(*s_iter1);
        if (s_iter2 != s2.end())
                ret.insert(*s_iter2);
    }
    return ret;
}


static void printSet (set<int>& s) {
    set<int>::iterator iter;
    for (iter=s.begin();iter!=s.end();iter++)
        cout << *iter << endl;
}

static void printVector (vector<int>& v) {    
    for (int i=0;i!=v.size();i++)
        cout << "   " << v[i] << endl;
}
#endif	/* TOOLS_H */


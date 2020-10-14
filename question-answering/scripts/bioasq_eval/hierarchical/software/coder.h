/* 
 * File:   coder.h
 * Author: aris
 *
 * Created on November 23, 2011, 5:46 PM
 */

#ifndef CODER_H
#define	CODER_H
#include <map>
#include <set>
using namespace std;
class Coder {
    map<int,int> c;
    map<int,int>::iterator iter;
    unsigned lastCode;
public:    
    Coder () {
        lastCode = 0;
    }
    void clear() {
        lastCode = 0;
        c.clear();
    }
    int getCode (int i) {
        iter = c.find(i);
        if (iter == c.end()) {
            lastCode ++;
            c[i] = lastCode;
            return lastCode;
        }
        else
            return iter->second;
    }
    
    void print(set<int>& s) {
        set<int>::iterator s_iter;
        for (s_iter=s.begin();s_iter!=s.end();s_iter++) {
            int id = *s_iter;
            int code = 0;
            iter = c.find(id);
            if (iter!=c.end())
                code = iter->second;
            cout << "Feature " << id << " = " << code << endl;
        }
    }
};

#endif	/* CODER_H */


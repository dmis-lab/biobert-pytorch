/* 
 * File:   NodeLength.h
 * Author: aris
 *
 * Created on December 5, 2011, 3:00 PM
 */

#ifndef NODELENGTH_H
#define	NODELENGTH_H

class NodeLength {
public:
    int node;
    double length;
    NodeLength (int n= 0, double l =0.0) {
        node = n;
        length = l;
    }
    bool operator< (const NodeLength& second) const{
        return length < second.length;
    }
};

#endif	/* NODELENGTH_H */


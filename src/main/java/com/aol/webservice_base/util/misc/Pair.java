/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aol.webservice_base.util.misc;

/**
 *
 * @author tjj (From http://stackoverflow.com/questions/779414/java-generics-pairstring-string-stored-in-hashmap-not-retrieving-key-value-pr )
 */

public class Pair<TYPEA, TYPEB> implements Comparable<Pair<TYPEA, TYPEB>> {

    protected final TYPEA first;
    protected final TYPEB second;

    public Pair(TYPEA first, TYPEB second) {
        this.first = first;
        this.second = second;
    }

    public TYPEA getFirst() {
        return first;
    }

    public TYPEB getSecond() {
        return second;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("first: ");
        buff.append(first);
        buff.append(" second: ");
        buff.append(second);
        return (buff.toString());
    }

    // @Override - Eclipse doesn't like @Override here
    public int compareTo(Pair<TYPEA, TYPEB> p1) {
        if (null != p1) {
            if (p1.equals(this)) {
                return 0;
            } else if (p1.hashCode() > this.hashCode()) {
                return 1;
            } else if (p1.hashCode() < this.hashCode()) {
                return -1;
            }
        }
        return (-1);
    }

    public boolean equals(Pair<TYPEA, TYPEB> p1) {
        if (null != p1) {
            if (p1.first.equals(this.first) && p1.second.equals(this.second)) {
                return (true);
            }
        }
        return (false);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<TYPEA, TYPEB> other = (Pair<TYPEA, TYPEB>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = first.hashCode() + (31 * second.hashCode());
        return (hashCode);
    }
}

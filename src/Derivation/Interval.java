/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Derivation;

/**
 *
 * @author sherzod
 */
public class Interval {

    private int startIndex;
    private int endIndex;

    public Interval(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public boolean contains(int i) {
        if (startIndex == i) {
            return true;
        }
        if (startIndex < i && i < endIndex) {
            return true;
        }
        if (endIndex == i) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return startIndex + ":" + endIndex;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.startIndex;
        hash = 17 * hash + this.endIndex;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Interval other = (Interval) obj;
        if (this.startIndex != other.startIndex) {
            return false;
        }
        if (this.endIndex != other.endIndex) {
            return false;
        }
        return true;
    }

}

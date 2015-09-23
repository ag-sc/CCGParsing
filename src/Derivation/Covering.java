/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Derivation;

import Grammar.CCGLexEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class Covering implements Comparable<Covering> {

    private HashMap<Interval, CCGLexEntry> lexicalEntries;
    private double score = 0.0;
    private List<CCGLexEntry> tokens = new ArrayList<CCGLexEntry>();

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Covering() {
        lexicalEntries = new HashMap<Interval, CCGLexEntry>();
    }

    public HashMap<Interval, CCGLexEntry> getLexicalEntries() {
        return lexicalEntries;
    }

    public void setLexicalEntries(HashMap<Interval, CCGLexEntry> lexicalEntries) {
        this.lexicalEntries = lexicalEntries;
    }

    public void addToken(CCGLexEntry lexicalEntry, Interval interval, double score) {
        this.score += score;

        lexicalEntries.put(interval, lexicalEntry);

    }

    public List<CCGLexEntry> sortedEntries() {
        List<CCGLexEntry> lexEntries = new ArrayList<>();

        Set<Entry<Interval, CCGLexEntry>> entries = lexicalEntries.entrySet();

        List<Integer> keys = new ArrayList<>();
        for (Entry entry : entries) {
            Interval i1 = (Interval) entry.getKey();
            CCGLexEntry e1 = (CCGLexEntry) entry.getValue();
            keys.add(i1.getStartIndex());
        }
        Collections.sort(keys);

        for (int k : keys) {
            for (Entry entry : entries) {
                Interval i1 = (Interval) entry.getKey();
                CCGLexEntry e1 = (CCGLexEntry) entry.getValue();
                if (i1.getStartIndex() == k) {
                    lexEntries.add(e1);
                    break;
                }
            }
        }

        return lexEntries;
    }

    public boolean covers(int i) {

        Set<Interval> keys = lexicalEntries.keySet();

        for (Interval interval : keys) {
            if (interval.contains(i)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int compareTo(Covering o) {
        return Double.compare(o.getScore(), getScore());
    }

    @Override
    public String toString() {
        String s = "Score: " + score + "\n";

        s += "Entries: \n\n";
        for (CCGLexEntry e : sortedEntries()) {
            s += e.toString()+"\n";
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.lexicalEntries);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
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
        final Covering other = (Covering) obj;
        if (!Objects.equals(this.lexicalEntries, other.lexicalEntries)) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        return true;
    }
    
    

    public Covering clone() {
        Covering c = new Covering();
        c.setScore(score);
        for (Interval i : lexicalEntries.keySet()) {
            c.addToken(lexicalEntries.get(i), i, 0);
        }
        for (CCGLexEntry e : tokens) {
            c.tokens.add(e);
        }
        //c.setLexicalEntries(lexicalEntries);
        return c;
    }

    public List<CCGLexEntry> getTokens() {
        return tokens;
    }

    public void setTokens(List<CCGLexEntry> tokens) {
        this.tokens = tokens;
    }

}

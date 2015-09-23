/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Derivation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class Span {
    private String ngram;
    private List<Integer> indices;

    public String getNGram() {
        return ngram;
    }

    public void setNGram(String ngram) {
        this.ngram = ngram;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public Span() {
        indices = new ArrayList<>();
    }
    
}

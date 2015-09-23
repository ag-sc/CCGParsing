/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Decoder;

import Derivation.Covering;
import Derivation.CoveringComparator;
import Feature.CCGFeature;
import Feature.Feature;
import Grammar.CCGLexEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author sherzod
 */
public class Bucket {

    private PriorityQueue<Covering> queue;
    private int size = 1000;

    public Bucket(int size) {
        queue = new PriorityQueue<Covering>();
        this.size = size;
    }

    public void add(Covering c) {
        queue.add(c);
    }

    /*
     * @return topK coverings in a priority queue
     * @param PriorityQueue<Covering> queue, int topK  
     */
    public void prune() {

        PriorityQueue<Covering> top = new PriorityQueue<Covering>();

        int count = 1;

        while (!queue.isEmpty() && count <= size) {
            Covering c = queue.poll();
            top.add(c);
            count++;
        }

        this.queue.clear();
        this.queue = top;
    }

    public List<Covering> getCoverings() {
        Object[] c = queue.toArray();

        List<Covering> coverings = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            coverings.add((Covering) c[i]);
        }
        return coverings;
    }

    public boolean contains(Covering c) {

        List<Covering> coverings = getCoverings();
//        for(Covering c1: coverings){
//            if(c1.equals(c)){
////                System.out.println(c1.toString());
////                System.out.println("\n\n"+c.toString());
//                return true;
//            }
//        }
        if (coverings.contains(c)) {
            
            return true;
        }
        return false;
    }

}

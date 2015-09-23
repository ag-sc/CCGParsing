/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Decoder;

import Derivation.Covering;
import Derivation.Interval;
import Feature.CCGFeature;
import Feature.Feature;
import Grammar.CCGLexEntry;
import Model.Model;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class StackDecoderOld implements Decoder {

    private boolean useInstancesBasedFeatures = true;
    private int stackSize = 1000;
    private int nGramSize = 3;
    private Model model;

    public StackDecoderOld(Model model, int stackSize, int nGramSize) {
        this.model = model;
        this.nGramSize = nGramSize;
        this.stackSize = stackSize;
    }

    @Override
    public void setUseInstanceBasedFeatures(boolean b) {
        this.useInstancesBasedFeatures = b;
    }

    @Override
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    public List<Covering> decode(String[] tokens, int indexOfInstance) {
        
        if(tokens.length>=10){
            this.stackSize = 10000;
        }
        List<Bucket> stacks = new ArrayList<Bucket>();
        List<Covering> coverings = new ArrayList<Covering>();

//        for (int i = 0; i < tokens.length; i++) {
//            Bucket s = new Bucket(stackSize);
//            stacks.add(s);
//        }
        for (int t = 0; t < tokens.length; t++) {
            try {
                Bucket stack = new Bucket(stackSize);

                for (int n = 1; n <= nGramSize; n++) {

                    int endIndex = t + (n - 1);

                    if (endIndex < tokens.length) {
                        String ngram = "";

                        for (int k = t; k <= endIndex; k++) {
                            ngram += tokens[k] + " ";
                        }

                        ngram = ngram.trim();
                        Interval v = new Interval(t, endIndex);

                        List<Feature> matchingFeatures = getMatchingEntries(ngram, model, indexOfInstance, useInstancesBasedFeatures);

                        if (ngram.equals("tree frogs")) {
                            int z = 1;
                        }

                        for (Feature m : matchingFeatures) {
                            CCGFeature feature = (CCGFeature) m;

                            CCGLexEntry c1 = new CCGLexEntry(feature.getWord(), feature.getCategory(), feature.getLambda());
                            c1.setInstanceId(indexOfInstance);

                            int nGramEffect = n;
                            if (n > 1) {
                                nGramEffect += 0.1;
                            }

                            if (stacks.isEmpty()) {

                                Covering covering = new Covering();

                                covering.addToken(c1, v, feature.getScore() * nGramEffect);

                                if (!stack.contains(covering)) {
                                    stack.add(covering);
                                }

                            } else {
                                List<Covering> previous = stacks.get(t - 1).getCoverings();
                                for (Covering p : previous) {

                                    Covering covering = p.clone();

                                    if (!covering.covers(t)) {
                                        covering.addToken(c1, v, feature.getScore() * nGramEffect);
                                    }
                                    if (!stack.contains(covering)) {
                                        stack.add(covering);
                                    }
                                }
                            }
                        }
                    }
                }

                stack.prune();
                if (stack.getCoverings().isEmpty()) {
                    if (t > 0) {
                        stacks.add(stacks.get(t - 1));
                    }
                } else {
                    stacks.add(stack);
                }
            } catch (Exception e) {
                System.err.println("Problem with token: " + tokens[t]);
            }
        }

        if(!stacks.isEmpty()){
            if(stacks.get(stacks.size() - 1).getCoverings()!=null){
                coverings = stacks.get(stacks.size() - 1).getCoverings();
            }
        }
        

        return coverings;
    }

    private List<Feature> getMatchingEntries(String ngram, Model model, int index, boolean useInstanceBasedFeatures) {
        List<Feature> matchingFeatures = new ArrayList<Feature>();
        //List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        if (ngram.equals("")) {
            return matchingFeatures;
        }

        if (ngram.equals("countries")) {
            int z = 1;
        }

        if (useInstanceBasedFeatures) {
            matchingFeatures = model.getEntriesforLexeme(ngram, -1);
            if (matchingFeatures == null) {
                matchingFeatures = model.getEntriesforLexeme(ngram, index);
            } else {
                //add non defined features too
                List<Feature> matchingFeaturesWithIndex = model.getEntriesforLexeme(ngram, index);
                if (matchingFeaturesWithIndex != null) {
                    for (Feature f : matchingFeaturesWithIndex) {
                        if (!matchingFeatures.contains(f)) {
                            matchingFeatures.add(f);
                        }
                    }
                }
            }

        } else {
            matchingFeatures = model.getEntriesforLexeme(ngram, true);
        }

        return matchingFeatures;
    }

}

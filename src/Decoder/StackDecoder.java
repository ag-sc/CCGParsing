/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Decoder;

import Derivation.Covering;
import Derivation.Derivation;
import Derivation.Interval;
import Feature.CCGFeature;
import Feature.Feature;
import Grammar.CCGLexEntry;
import Instance.Instance;
import Lemmatizer.Lemmatizer;
import Model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class StackDecoder implements Decoder {

    List<Bucket> stacks;

    int nGramSize;

    int stackSize;

    Model model;

    boolean instanceFeatures = true;

    public StackDecoder(Model model,int stackSize, int nGramSize) {
        stacks = new ArrayList<Bucket>();
        this.nGramSize = nGramSize;
        this.model = model;
        this.stackSize = stackSize;
    }

    public List<Covering> decode(String[] sentence, int indexOfInstance) {

        Bucket stack = null;
        List<Covering> coverings = new ArrayList<Covering>();

        //initialize stacks
        for (int i = 0; i < sentence.length; i++) {
            Bucket s = new Bucket(stackSize);
            stacks.add(s);
        }

        for (int j = 1; j <= stacks.size(); j++) {
            stack = stacks.get(j-1);

            if (j <= nGramSize) {
                //stack.add(all coverings of size j
                HashMap<Interval, String> ngrams = getNGrams(sentence, j);
                for (Interval v : ngrams.keySet()) {
                    String ngram = ngrams.get(v);
                    List<Feature> matchingFeatures = getMatchingEntries(ngram, model, indexOfInstance, instanceFeatures);

                    for (Feature f1 : matchingFeatures) {
                        if (f1 instanceof CCGFeature) {

                            CCGFeature feature = (CCGFeature) f1;
                            CCGLexEntry c1 = new CCGLexEntry(feature.getWord(), feature.getCategory(), feature.getLambda());
                            c1.setInstanceId(indexOfInstance);

                            Covering covering = new Covering();
                            int nGramEffect = j;
                            if(j>1){
                                nGramEffect += 0.1;
                            }
                            covering.addToken(c1, v, feature.getScore() * nGramEffect);

                            if(!stack.contains(covering)){
                                stack.add(covering);
                            }
                        }
                    }
                }
            }

            stack.prune();

            for (Covering covering : stack.getCoverings()) {
                for (int i = 0; i < sentence.length; i++) {
                    if (!covering.covers(i)) {
                        // retrieve all enries from DB covering token i;
                        // fo each entry
                        // create new covering, add it to stack i+1;

                        String ngram = sentence[i];
                        Interval interval = new Interval(i, i);

                        List<Feature> matchingFeatures = getMatchingEntries(ngram, model, indexOfInstance, instanceFeatures);

                        for (Feature f1 : matchingFeatures) {
                            if (f1 instanceof CCGFeature) {

                                CCGFeature feature = (CCGFeature) f1;
                                CCGLexEntry c1 = new CCGLexEntry(feature.getWord(), feature.getCategory(), feature.getLambda());

                                Covering newCovering = covering.clone();
                                newCovering.addToken(c1, interval, feature.getScore());

                                if (j <= stacks.size()) {
                                    if (!stacks.get(j).contains(newCovering)) {
                                        stacks.get(j).add(newCovering);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int z = 1;

        }

        Bucket lastStack = stacks.get(stacks.size() - 1);
        lastStack.prune();
        coverings = lastStack.getCoverings();

        return coverings;
    }

    public HashMap<Interval, String> getNGrams(String[] tokens, int nGramLength) {

        HashMap<Interval, String> ngrams = new HashMap<Interval, String>();

        for (int t = 0; t < tokens.length; t++) {

            int endIndex = t + (nGramLength - 1);

            if (endIndex < tokens.length) {
                String ngram = "";

                for (int k = t; k <= endIndex; k++) {
                    ngram += tokens[k] + " ";
                }
                ngram = ngram.trim();

                if (!ngram.equals("")) {
                    ngrams.put(new Interval(t, endIndex), ngram);
                }
            }
        }

        return ngrams;
    }

    public void setUseInstanceBasedFeatures(boolean b) {
        this.instanceFeatures = b;
    }

    private List<Feature> getMatchingEntries(String ngram, Model model, int index, boolean useInstanceBasedFeatures) {
        List<Feature> matchingFeatures = new ArrayList<Feature>();
        //List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        if (ngram.equals("")) {
            return matchingFeatures;
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

    @Override
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

}

package Trainer;

import Derivation.CCGDerivation;
import Derivation.Derivation;
import Evaluator.Evaluator;
import Feature.FeatureExtractor;
import Grammar.CCGLexEntry;
import Grammar.Grammar;
import Instance.Instance;
import Instance.SemanticParsingInstance;
import Model.Model;
import Reader.QALD.SPARQLParser;
import java.util.ArrayList;
import java.util.List;

public class Perceptron implements Trainer {

    Model model;

    Evaluator evaluator;

    FeatureExtractor extractor;

    boolean updateAllFeatures = true;

    public boolean isUpdateAllFeatures() {
        return updateAllFeatures;
    }

    public void setUpdateAllFeatures(boolean updateAllFeatures) {
        this.updateAllFeatures = updateAllFeatures;
    }

    public Perceptron(Evaluator evaluator, Model model, FeatureExtractor extractor, boolean updateAllFeatures) {
        this.model = model;
        this.evaluator = evaluator;
        this.extractor = extractor;
        this.updateAllFeatures = updateAllFeatures;
    }

    @Override
    public List<Instance> train(List<Instance> training) {

        

            for (Instance instance : training) {
                try {

                    SemanticParsingInstance ins = (SemanticParsingInstance) instance;

                    if (ins.getDerivation() != null) {

                        if (ins.getDerivation().size() > 0) {

                            if (updateAllFeatures) {
                                updateAllFeatures(instance);
                            } else {
                                //at Step2 only instance that are parsed will run!!!
                                instance = updateUsedFeatures(instance);
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Problem with training instances !!! " + e.getMessage());
                }
            }
        

        return training;
    }

    private void updateAllFeatures(Instance i) {
        SemanticParsingInstance ins = (SemanticParsingInstance) i;
        SPARQLParser parser = new SPARQLParser();

        String s = parser.convertLogicalFormToQuery(ins.getForm());
        
        CCGDerivation expectedDerivation = null;
        for (Derivation m : ins.getDerivation()) {
            CCGDerivation d = (CCGDerivation) m;

            String s1 = d.getExpression().reduce().toString();
            if(s1.equals("lambda Var-x([Pred-http://www.w3.org/1999/02/22-rdf-syntax-ns#type(Var-x Cons-http://dbpedia.org/ontology/Ship) and Pred-http://dbpedia.org/property/shipNamesake(Var-x Cons-http://dbpedia.org/resource/Benjamin_Franklin)])")){
                int z=1;
            }
            //System.out.println(s1);
            
            //get the correct derivation
            if (evaluator.betterOrEqualTo(d.getExpression().reduce(), ins.getForm())) {
                ins.setIsParsed(true);
                expectedDerivation = d;
                break;
            }
        }

        if (expectedDerivation != null) {

            boolean isUpdated = false;
            for (Derivation m : ins.getDerivation()) {

                CCGDerivation d = (CCGDerivation) m;

                if (d != expectedDerivation) {
                    //check if derivation is equal to the expected semantics
                    boolean isBetter = evaluator.betterOrEqualTo(d.getExpression(), ins.getForm());
                    if (!isBetter) {

                        try {
                            //increase score of the features in the correct derivation
                            // You should not leave your computer unattended. One error has been introduced at line ... (by Timo)
                            model.updateAllFeatures(extractor.extractFeatures(expectedDerivation), 1.0);

                            //penalize the score of features in the incorrect derivation
                            model.updateAllFeatures(extractor.extractFeatures(d), -1.0);

                            // Make sure that this is actually updated! Check this later. (by Janik)
                            isUpdated = true;

                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }

            if (ins.getDerivation().size() == 1) {
                model.updateAllFeatures(extractor.extractFeatures(expectedDerivation), 1.0);
                isUpdated = true;
            }
            if (!isUpdated) {
                model.updateAllFeatures(extractor.extractFeatures(expectedDerivation), 1.0);
            }
        } else {//if there is not any derivation with the right semantics, penalize all derivations
            for (Derivation m : ins.getDerivation()) {

                CCGDerivation d = (CCGDerivation) m;

                try {
                    //penalize the score of features in the incorrect derivation
                    model.updateAllFeatures(extractor.extractFeatures(d), -1.0);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    private Instance updateUsedFeatures(Instance i) {
        SemanticParsingInstance ins = (SemanticParsingInstance) i;

        for (Derivation m : ins.getDerivation()) {
            CCGDerivation d = (CCGDerivation) m;

//            //get the correct derivation
//            if (evaluator.betterOrEqualTo(d.getExpression(), ins.getForm())) {
//                ins.setIsParsed(true);
//                try {
//                    //increase score of the features in the correct derivation
//                    model.updateAllFeatures(extractor.extractFeatures(d), 0.0);
//                    
//                } catch (Exception ex) {
//                    System.err.println(ex.getMessage());
//                }
//            }
            try {
                ins.setIsParsed(true);
                //increase score of the features in the correct derivation
                model.updateAllFeatures(extractor.extractFeatures(d), 0.0);

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        return ins;

    }
}

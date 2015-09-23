/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Feature;

import Derivation.CCGDerivation;
import Derivation.Derivation;
import Grammar.CCGLexEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class SemanticFeatureExtractor implements FeatureExtractor{

    @Override
    public List<Feature> extractFeatures(Derivation max) {
        List<Feature> features = new ArrayList<Feature>();
        CCGDerivation d = (CCGDerivation) max;
        for(CCGLexEntry e : d.getEntries()){
            CCGFeature sf = new CCGFeature(e.getLambda(), e.getLemma(), e.getSyntax());
            features.add(sf);
        }        
        return features;
    }

    @Override
    public List<Feature> extractFeatures(List<CCGLexEntry> entries) {
        List<Feature> features = new ArrayList<Feature>();
        for(CCGLexEntry e : entries){
            CCGFeature sf = new CCGFeature(e.getLambda(), e.getLemma(), e.getSyntax());
            sf.setInstanceId(e.getInstanceId());
            features.add(sf);
        }        
        return features;
    }
    
}

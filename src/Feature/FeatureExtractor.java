package Feature;

import Derivation.Derivation;
import Grammar.CCGLexEntry;

import Instance.Instance;
import java.util.List;
import java.util.Set;

public interface FeatureExtractor {

	List<Feature> extractFeatures(Derivation max);
        List<Feature> extractFeatures(List<CCGLexEntry> entries);

}

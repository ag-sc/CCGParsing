package Model;

import Feature.CCGFeature;
import Feature.Feature;
import Lemmatizer.Lemmatizer;
import java.util.List;
import java.util.Set;


public interface Model {

	public void updateAllFeatures(List<Feature> feature, Double value);
	
	public void udpateExistingFeature(Feature feature);
        
        public List<Feature> getEntriesforLexeme(String lexeme, int index);
        
        public List<Feature> getEntriesforLexeme(String lexeme, boolean isUsed);
        
        public List<Feature> getEntriesforLexeme(String lexeme);
}

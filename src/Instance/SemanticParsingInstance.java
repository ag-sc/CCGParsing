package Instance;

import Derivation.Derivation;
import LambdaCalculus.Expression;
import Model.Model;
import java.util.List;

public class SemanticParsingInstance implements Instance {

    String Sentence;
    
    String geobaseForm;

    Expression Form;

    List<Derivation> derivation;
    
    boolean isParsed = false;
    
    int lastSentenceIndex;
    
    public int getLastSentenceIndex() {
        return lastSentenceIndex;
    }

    public void setLastSentenceIndex(int lastSentenceIndex) {
        this.lastSentenceIndex = lastSentenceIndex;
    }
    
    

    public boolean isParsed() {
        return isParsed;
    }

    public void setIsParsed(boolean isParsed) {
        this.isParsed = isParsed;
    }

    public SemanticParsingInstance(String sentence, Expression form) {
        Sentence = sentence;
        Form = form;
    }

    public List<Derivation> getDerivation() {
        return derivation;
    }

    public Expression getForm() {
        return Form;
    }

    public void setDerivation(List<Derivation> derivation) {
        this.derivation = derivation;
    }

    public String getSentence() {
        return Sentence;
    }

    public String getGeobaseForm() {
        return geobaseForm;
    }

    public void setGeobaseForm(String geobaseForm) {
        this.geobaseForm = geobaseForm;
    }

    @Override
    public Double getScore(Model model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String toString(){
        String s = getSentence();
        if(getForm()!=null){
            s+="\n\n"+getForm().toString()+"\n\n";
        }
        
        
        return s;
    }

}

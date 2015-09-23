package Derivation;

import Grammar.CCGLexEntry;
import LambdaCalculus.Expression;
import java.util.ArrayList;
import java.util.List;

public class CCGDerivation implements Derivation {
    List<CCGLexEntry> entries;
    int lastIndex;
    Expression expression;
    private String parseStep;
    boolean isParsed = false;
    double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isParsed() {
        return isParsed;
    }

    public void setIsParsed(boolean isParsed) {
        this.isParsed = isParsed;
    }
    
    

    public String getParseStep() {
        return parseStep;
    }

    public void setParseStep(String parseStep) {
        this.parseStep = parseStep;
    }
    
    public CCGDerivation() {
        this.entries= new ArrayList<>();
    }

    public CCGDerivation(List<CCGLexEntry> entries) {
        this.entries = entries;
    }

    public void setEntries(List<CCGLexEntry> entries) {
        this.entries = entries;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public List<CCGLexEntry> getEntries() {
        return entries;
    }
    
    public CCGDerivation cloneTo(){
        CCGDerivation d = new CCGDerivation();
        d.setLastIndex(getLastIndex());
        List<CCGLexEntry> entries = new ArrayList<>();
        for(CCGLexEntry e : getEntries()){
            entries.add(e.clone());
        }
        d.setEntries(entries);
        
        if(this.expression!=null){
            d.setExpression(this.expression);
        }
        d.setIsParsed(isParsed());
        d.setParseStep(parseStep);
        d.setScore(score);

        return d;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        if(expression!=null){
            this.expression = expression;
        }
    }

    @Override
    public String toString() {
        String s = "Score: " + score + "\n";

        s += "Entries: \n\n";
        for (CCGLexEntry e : getEntries()) {
            s += e.toString()+"\n";
        }
        
        s+="\n\n"+expression.reduce();
        s+="\n"+parseStep;

        return s;
    }
    
    
    
    
}

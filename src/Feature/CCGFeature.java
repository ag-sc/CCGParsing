/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Feature;

import Grammar.CCGCategory;
import Grammar.CCGLexEntry;
import LambdaCalculus.Expression;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author sherzod
 */
public class CCGFeature implements Feature, Serializable, Comparable<CCGFeature> {

    private Expression lambda;
    private String word;
    private CCGCategory category;
    private double score = 0;
    private int instanceId = -1;
    private boolean isUsed = false;

    public CCGFeature(Expression lambda, String word, CCGCategory ccg) {
        this.lambda = lambda;
        this.word = word;
        this.category = ccg;
    }
    
    public CCGFeature(Expression lambda, String word, CCGCategory ccg, double score) {
        this.lambda = lambda;
        this.word = word;
        this.category = ccg;
        this.score = score;
    }
    public CCGFeature(Expression lambda, String word, CCGCategory ccg, double score, boolean used) {
        this.lambda = lambda;
        this.word = word;
        this.category = ccg;
        this.score = score;
        this.isUsed = used;
    }
    public CCGFeature(Expression lambda, String word, CCGCategory ccg, double score, boolean used, int instanceId) {
        this.lambda = lambda;
        this.word = word;
        this.category = ccg;
        this.score = score;
        this.isUsed = used;
        this.instanceId = instanceId;
    }

    public CCGFeature() {
    }

    public Expression getLambda() {
        return lambda;
    }

    public void setLambda(Expression lambda) {
        this.lambda = lambda;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public CCGCategory getCategory() {
        return category;
    }

    public void setCategory(CCGCategory category) {
        this.category = category;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "word=" + word + "  lambda=" + lambda + "  category=" + category + "   score=" + score +"  instance id="+instanceId;
    }

    public CCGFeature clone() {
        
        CCGFeature f = new CCGFeature(lambda, word, category);
        f.setScore(getScore());
        f.setInstanceId(getInstanceId());
        f.setIsUsed(isUsed());

        return f;
    }

    @Override
    public int compareTo(CCGFeature o) {
        double delta = getScore() - o.getScore();
        if (delta > 0) {
            return -1;
        }
        if (delta < 0) {
            return 1;
        }
        return 0;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.lambda);
        hash = 37 * hash + Objects.hashCode(this.word);
        hash = 37 * hash + Objects.hashCode(this.category);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 37 * hash + this.instanceId;
        hash = 37 * hash + (this.isUsed ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CCGFeature other = (CCGFeature) obj;
        if (!Objects.equals(this.lambda, other.lambda)) {
            return false;
        }
        if (!Objects.equals(this.word, other.word)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (this.instanceId != other.instanceId) {
            return false;
        }
        if (this.isUsed != other.isUsed) {
            return false;
        }
        return true;
    }

}

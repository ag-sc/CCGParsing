package Grammar;

import LambdaCalculus.Expression;
import java.util.Objects;

public class CCGLexEntry {

    String Lemma;

    CCGCategory Syntax;

    Expression Lambda;

    int instanceId = -1;

    public CCGLexEntry(String lemma, CCGCategory syntax, Expression lambda) {
        Lemma = lemma;
        Syntax = syntax;
        Lambda = lambda;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public String toString() {
        return Lemma + " " + Syntax + " " + Lambda;
    }

    public String getLemma() {
        return Lemma;
    }

    public void setLemma(String Lemma) {
        this.Lemma = Lemma;
    }

    public void setSyntax(CCGCategory Syntax) {
        this.Syntax = Syntax;
    }

    public void setLambda(Expression Lambda) {
        this.Lambda = Lambda;
    }

    public CCGCategory getSyntax() {
        return Syntax;
    }

    public Expression getLambda() {
        return Lambda;
    }

    public CCGLexEntry clone() {
        return new CCGLexEntry(this.Lemma, this.Syntax, this.Lambda);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.Lemma);
        hash = 53 * hash + Objects.hashCode(this.Syntax);
        hash = 53 * hash + Objects.hashCode(this.Lambda);
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
        final CCGLexEntry other = (CCGLexEntry) obj;
        if (!Objects.equals(this.Lemma, other.Lemma)) {
            return false;
        }
        if (!Objects.equals(this.Syntax, other.Syntax)) {
            return false;
        }
        if (!Objects.equals(this.Lambda, other.Lambda)) {
            return false;
        }
        return true;
    }

    

}

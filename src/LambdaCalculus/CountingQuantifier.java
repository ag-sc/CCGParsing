package LambdaCalculus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CountingQuantifier extends PredicateLogicalForm {

    public CountingQuantifier(Expression c, Quantor quantor) {
        this.condition = c;
        this.quantor = quantor;
    }

    public CountingQuantifier() {
        this.quantor = CountingQuantifier.Quantor.count;
    }
    
    
    @Override
    public Expression reduce() {
        Expression c = condition.reduce();
        
        return new CountingQuantifier(c, quantor);
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {
        Expression c = condition.substitute(var, exp);
        return new CountingQuantifier(c, quantor);
    }
    
    public enum Quantor {
        count
    };

    Expression condition;

    Quantor quantor;

    @Override
    public String toString() {
        return quantor  + " ["+condition.toString()+"]";
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Quantor getQuantor() {
        return quantor;
    }

    public void setQuantor(Quantor quantor) {
        this.quantor = quantor;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.condition);
        hash = 97 * hash + Objects.hashCode(this.quantor);
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
        final CountingQuantifier other = (CountingQuantifier) obj;
        if (!Objects.equals(this.condition, other.condition)) {
            return false;
        }
        if (this.quantor != other.quantor) {
            return false;
        }
        return true;
    }


    
}

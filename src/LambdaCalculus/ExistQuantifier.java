package LambdaCalculus;

import java.util.Objects;


public class ExistQuantifier extends PredicateLogicalForm {

    public ExistQuantifier(Expression c, Quantor quantor) {
        this.condition = c;
        this.quantor = quantor;
    }

    public ExistQuantifier(Expression c) {
        this.quantor = ExistQuantifier.Quantor.ask;
        this.condition = c;
    }

    public ExistQuantifier() {
        this.quantor = ExistQuantifier.Quantor.ask;
    }
    
    

    @Override
    public Expression reduce() {
        Expression c = condition.reduce();
        return new ExistQuantifier(c, this.quantor);
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {
        Expression c = condition.substitute(var, exp);
        return new ExistQuantifier(c, quantor);
    }

    public enum Quantor {
        ask
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
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.condition);
        hash = 83 * hash + Objects.hashCode(this.quantor);
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
        final ExistQuantifier other = (ExistQuantifier) obj;
        if (!Objects.equals(this.condition, other.condition)) {
            return false;
        }
        if (this.quantor != other.quantor) {
            return false;
        }
        return true;
    }
    

}

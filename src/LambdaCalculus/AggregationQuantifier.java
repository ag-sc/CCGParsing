package LambdaCalculus;

import java.util.Objects;

public class AggregationQuantifier extends Function {

    public AggregationQuantifier(Expression c, Expression f, Quantor Quant, Variable v) {
        this.condition = c;
        this.aggregationExpression = f;
        this.Quant = Quant;
        this.aggregationVariable = v;
    }

    @Override
    public Expression reduce() {
        Expression c = condition.reduce();
        Expression f = aggregationExpression.reduce();
        
        return new AggregationQuantifier(c, f, Quant, aggregationVariable);
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {

        Expression c = condition.substitute(var, exp);
        Expression f = aggregationExpression.substitute(var, exp);
        
        
        return new AggregationQuantifier(c.reduce(), f.reduce(), Quant, aggregationVariable);

    }

    public enum Quantor {

        argmin, argmax
    };

    Expression condition;

    Expression aggregationExpression;

    Variable aggregationVariable;

    Quantor Quant;

    @Override
    public String toString() {
        String s = "";

        s += Quant.toString() + " " + aggregationVariable.toString() + " ";
        s += "(" + condition.toString() + ")" + " (" + aggregationExpression.toString() + ")";

        return s;
    }

    public AggregationQuantifier() {
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getAggregationExpression() {
        return aggregationExpression;
    }

    public void setAggregationExpression(Expression aggregationExpression) {
        this.aggregationExpression = aggregationExpression;
    }

    public Variable getAggregationVariable() {
        return aggregationVariable;
    }

    public void setAggregationVariable(Variable aggregationVariable) {
        this.aggregationVariable = aggregationVariable;
    }

    public Quantor getQuant() {
        return Quant;
    }

    public void setQuant(Quantor Quant) {
        this.Quant = Quant;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.condition);
        hash = 79 * hash + Objects.hashCode(this.aggregationExpression);
        hash = 79 * hash + Objects.hashCode(this.aggregationVariable);
        hash = 79 * hash + Objects.hashCode(this.Quant);
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
        final AggregationQuantifier other = (AggregationQuantifier) obj;
        if (!Objects.equals(this.condition, other.condition)) {
            return false;
        }
        if (!Objects.equals(this.aggregationExpression, other.aggregationExpression)) {
            return false;
        }
        if (!Objects.equals(this.aggregationVariable, other.aggregationVariable)) {
            return false;
        }
        if (this.Quant != other.Quant) {
            return false;
        }
        return true;
    }

}

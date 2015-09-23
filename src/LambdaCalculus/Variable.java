package LambdaCalculus;

import java.util.Objects;


public class Variable implements Term, Expression {

    String value;

    public Variable(String value) {
        this.value = value;
    }



    @Override
    public String toString() {
        return "Var-"+value;
    }

    @Override
    public Expression reduce() {
        return this;
    }

    @Override
    public Expression substitute(Variable var, Expression exp) {
        if(value.equals(var.value)){
            return exp;
        }
        
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Term clone() {
        return new Variable(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.value);
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
        final Variable other = (Variable) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    
}

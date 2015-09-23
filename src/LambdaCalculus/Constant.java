package LambdaCalculus;

import java.io.Serializable;
import java.util.Objects;

public class Constant implements Term, Expression {

    String value;


    public Constant(String value) {
        this.value = value;
    }



    @Override
    public String toString() {
        return "Cons-"+value;
    }

    @Override
    public Expression reduce() {
        return this;
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {
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
        return new Constant(value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.value);
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
        final Constant other = (Constant) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}

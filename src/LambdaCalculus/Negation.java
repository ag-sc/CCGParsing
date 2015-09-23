/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package LambdaCalculus;

import java.util.Objects;

/**
 *
 * @author sherzod
 */
public class Negation extends PredicateLogicalForm implements Expression {
    
    Expression body;

    @Override
    public Expression reduce() {
        return this;
    }

    @Override
    public Expression substitute(Variable var, Expression exp) {
        Expression r = body.substitute(var, exp);
        
        return r;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.body);
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
        final Negation other = (Negation) obj;
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }
    
    


}

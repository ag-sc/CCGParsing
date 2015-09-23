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
public class LambdaAbstraction implements Expression {
    
    private Variable var;
    private Expression body;

    private LambdaAbstraction(Variable v, Expression b) {
        this.body = b;
        this.var = v;
    }

    public LambdaAbstraction() {
        
    }



    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    public Expression getBody() {
        return body;
    }

    public void setBody(Expression body) {
        this.body = body;
    }

    
    public String toString()
    {
        if(body instanceof Atom){
            return "lambda "+ var.toString() + "(["+body.toString()+"])";
        }
        return "lambda "+ var.toString() + "("+body.toString()+")";
    }

    @Override
    public Expression reduce() {
        return new LambdaAbstraction((Variable) this.var.clone(), this.body.reduce());
    }

    @Override
    public Expression substitute(Variable var, Expression exp) {
        if(!this.var.toString().equals(var.toString())){
            return new LambdaAbstraction(this.var, body.substitute(var, exp));
        }
        return this.reduce();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.var);
        hash = 13 * hash + Objects.hashCode(this.body);
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
        final LambdaAbstraction other = (LambdaAbstraction) obj;
        if (!Objects.equals(this.var, other.var)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }
}

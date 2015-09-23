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
public class FunctionalApplication implements Expression {

    private Expression function;

    private Expression argument;

    @Override
    public Expression reduce() {

        Expression f =  function.reduce();
        Expression a =  argument.reduce();
        
        if(a.toString().equals("lambda Var-P(Var-P)")){
            return f;
        }
        if(f.toString().equals("lambda Var-P(Var-P)")){
            return a;
        }
        
//        System.out.println(function2.toString());
        
        if (f instanceof LambdaAbstraction) {

            LambdaAbstraction function1 = (LambdaAbstraction) f;

            
            Expression r = function1.getBody().substitute(function1.getVar(), a);
            
            r = r.reduce();
            
            return r;
        }
        FunctionalApplication app = new FunctionalApplication();
        app.setArgument(a);
        app.setFunction(f);
        
        return app;
    }

    @Override
    public Expression substitute(Variable var, Expression exp) {
        FunctionalApplication a = new FunctionalApplication();
        a.setArgument(argument.substitute(var, exp));
        a.setFunction(function.substitute(var, exp));
        
        return a;

    }

    public Expression getFunction() {
        return function;
    }

    public void setFunction(Expression function) {
        this.function = function;
    }

    public Expression getArgument() {
        return argument;
    }

    public void setArgument(Expression argument) {
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "{" + function + "}@" + argument;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.function);
        hash = 13 * hash + Objects.hashCode(this.argument);
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
        final FunctionalApplication other = (FunctionalApplication) obj;
        if (!Objects.equals(this.function, other.function)) {
            return false;
        }
        if (!Objects.equals(this.argument, other.argument)) {
            return false;
        }
        return true;
    }

}

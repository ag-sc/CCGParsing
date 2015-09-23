/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package LambdaCalculus;

/**
 *
 * @author sherzod
 */
public interface Expression {
    public Expression reduce();
    public Expression substitute(Variable var, Expression exp);

}

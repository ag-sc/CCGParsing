/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Factory;

import LambdaCalculus.Expression;


/**
 *
 * @author sherzod
 */
public class Test {
    
    public static void main(String[] args){
        String s = "http://berlin";
                //+ "select  (g1-Atom,g2-Atom,?v2-Term) [multi (g1-Atom,g2-Atom,?v2-Term) [ ?v2-Term{g1(?v2)} and ?v2-Term{g2(?v2)} ]]";
        
        Factory f = new Factory();
        Expression e = f.constructLambdaCalculusExpression(s);
        
        System.out.println(e.toString());
        
    }
    
}

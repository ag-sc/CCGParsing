/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LambdaCalculus.Parser;

import Factory.Factory;
import Grammar.CombinedCategory;
import LambdaCalculus.AggregationQuantifier;
import LambdaCalculus.Constant;
import LambdaCalculus.Expression;
import LambdaCalculus.FunctionalApplication;
import LambdaCalculus.LambdaAbstraction;
import LambdaCalculus.Variable;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args) {
        List<String> text = new ArrayList<>();
//        text.add("[P-P(?x ?y)]");
//        text.add("Pred-Q(Var-a Var-b)");
//        text.add("count [[Pred-Q(Var-a Var-b) and [Pred-Q(Var-a Var-b) and Pred-Z(Var-a Var-b)]]]");
//        text.add("ask [[Pred-Q(Var-a Var-b) and [Pred-Q(Var-a Var-b) and Pred-Z(Var-a Var-b)]]]");
//        text.add("[Pred-P(Var-x Cons-y) and Pred-Q(Var-a Var-b)]");
//        text.add("lambda Var-x([Pred-P(Var-x Cons-y) and Pred-Q(Var-a Var-b)])");
//        text.add("lambda Var-x([Pred-P(Var-x Cons-y)])");
//        text.add("lambda Var-y ( lambda Var-x ( [Pred-P(Var-x Cons-y) and Pred-Q(Var-a Var-b)] ))");
//        text.add("argmax Var-x(Pred-P(Var-x Cons-y)) (Pred-Q(Var-a Var-b))");
//        text.add("lambda Var-x(argmax Var-x (Pred-P(Var-x Cons-y)) (Pred-Q(Var-a Var-b)))");
//        text.add("lambda Var-g(lambda Var-h(lambda Var-x([{Var-g}@Var-x and {Var-h}@Var-x])))");
//        text.add("lambda Var-g(lambda Var-x (count ({Var-g}@Var-x)))");
//        text.add("Cons-x");
//        text.add("lambda Var-g(lambda Var-h(lambda Var-x(count [{Var-g}@Var-x and {Var-h}@Var-x])))");
//        text.add("lambda Var-x(lambda Var-g(ask [{Var-g}@Var-x ]))");
//        text.add("lambda Var-x(lambda Var-g({Var-g}@Var-x))");
//        text.add("lambda Var-P(Var-P)");
//        text.add("Cons-'Frank The Tank'@en");

        for (String t : text) {

            try {

                StringReader sr = new StringReader(t);

                java.io.Reader r = new java.io.BufferedReader(sr);

                ExpressionParser parser = new ExpressionParser(r);

                Expression expr = (Expression) parser.parse();

                System.out.println(expr.toString());

            } catch (NullPointerException e) {
                //e.printStackTrace();
            } catch (ParseException e) {
                System.err.println(t);
                e.printStackTrace();
            }
        }

        Factory f = new Factory();

        LambdaAbstraction a1 = (LambdaAbstraction) f.constructLambdaCalculusExpression("lambda Var-y([Pred-http://dbpedia.org/ontology/developer(Cons-http://dbpedia.org/resource/Minecraft Var-y)])");
        Constant a2 = (Constant) f.constructLambdaCalculusExpression("Cons-http://dbpedia.org/resource/Minecraft");
        
//        FunctionalApplication app = new FunctionalApplication();
//        app.setFunction(a1);
//        app.setArgument(a2);
//        
//        System.out.println(app.toString());
//        //System.out.println(app.reduce());

        LambdaAbstraction a3 = (LambdaAbstraction) f.constructLambdaCalculusExpression("lambda Var-P(Var-P)");
        LambdaAbstraction a4 = (LambdaAbstraction) f.constructLambdaCalculusExpression("lambda Var-P(Var-P)");
        
        FunctionalApplication app2 = new FunctionalApplication();
        app2.setFunction(a1);
        app2.setArgument(a3);
        
        System.out.println(app2.toString());
        System.out.println(app2.reduce());

    }
}

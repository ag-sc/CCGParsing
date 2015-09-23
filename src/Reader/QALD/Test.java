/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reader.QALD;

import Evaluator.QALD_Evaluator;
import LambdaCalculus.Atom;
import LambdaCalculus.Constant;
import LambdaCalculus.ExistQuantifier;
import LambdaCalculus.Expression;
import static Reader.QALD.QALD.getQuestions;
import java.util.ArrayList;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args) {
        SPARQLParser parser = new SPARQLParser();

        Atom a = new Atom("class");
        a.add(new Constant("S"));
        a.add(new Constant("O"));

        ExistQuantifier e = new ExistQuantifier();
        e.setCondition(a);
        e.setQuantor(ExistQuantifier.Quantor.ask);

        String q = parser.convertLogicalFormToQuery(e);
        System.out.println(q);
    }
}

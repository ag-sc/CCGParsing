/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parser;

import Derivation.CCGDerivation;
import Derivation.Covering;
import Derivation.Derivation;
import Evaluator.QALD_Evaluator;
import Grammar.CCGLexEntry;
import Grammar.Grammar;
import Instance.Instance;
import Instance.SemanticParsingInstance;
import LambdaCalculus.Expression;
import Logging.Logger;
import Model.CCGModelOnSQL;
import Reader.QALD.SPARQLParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class SyntacticParser implements Parser{

    @Override
    public List<Derivation> parse(List<Covering> coverings) {

        CYKParser cyk = new CYKParser();

        QALD_Evaluator evaluator = new QALD_Evaluator();

        SPARQLParser parser = new SPARQLParser();

        CCGDerivation max = null;

        List<Derivation> derivations = new ArrayList<Derivation>();

        try {
            for (Covering c : coverings) {
                List<Derivation> returned = cyk.parse(c);
                if (!returned.isEmpty()) {
                    for (Derivation d1 : returned) {
                        CCGDerivation derivation = (CCGDerivation) d1;
                        Expression e = derivation.getExpression().reduce();
                        
                        String query = parser.convertLogicalFormToQuery(e);

//                        if (evaluator.isValidSPARQL(e)) {
                            derivations.add(d1);
//                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return derivations;
    }
    
}

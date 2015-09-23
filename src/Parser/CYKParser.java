/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import Database.DBManagement;
import Derivation.CCGDerivation;
import Derivation.Covering;
import Derivation.Derivation;
import Factory.Factory;
import Grammar.AtomicCategory;
import Grammar.CCGCategory;
import Grammar.CCGGrammar;
import Grammar.CCGLexEntry;
import Grammar.CombinedCategory;
import Grammar.Grammar;
import Instance.Instance;
import LambdaCalculus.Expression;
import LambdaCalculus.FunctionalApplication;
import LambdaCalculus.LambdaAbstraction;
import LambdaCalculus.Variable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author sherzod
 */
public class CYKParser {

    Factory factory = new Factory();
    
    public List<Derivation> parse(Covering d) {

        List<Derivation> derivations = new ArrayList<Derivation>();


        ArrayList<String> categories = new ArrayList<String>();

        List<CCGLexEntry> tokens = d.sortedEntries();
        
//
        for (CCGLexEntry e : tokens) {
            categories.add(e.getSyntax().toString());
        }

        CCGLexEntry[][] parse = new CCGLexEntry[tokens.size()][tokens.size()];
        String[][] trace = new String[tokens.size()][tokens.size()];

        //initialize array with syntactic categories and put in diagonal
        for (int i = 0; i < tokens.size(); i++) {
            parse[i][i] = tokens.get(i);
        }
        //initialize trace array
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = 0; j < tokens.size(); j++) {
                trace[i][j] = "";
            }
        }
        //put numbers of syntactic categories in diagonal
        for (int i = 0; i < tokens.size(); i++) {
            trace[i][i] = i + "";
        }

        for (int i = 1; i <= parse.length - 1; i++) { //columns = x
            for (int j = i - 1; j >= 0; j--) { //rows = y
                for (int h = j; h <= i - 1; h++) {

                    if (parse[j][h] != null && parse[h + 1][i] != null) {

                        CCGLexEntry first = parse[j][h];
                        CCGLexEntry second = parse[h + 1][i];

                        CCGLexEntry combined = combineCCG(first, second);

                        if (combined != null) {
                            if (combined.getSyntax().toString().equals("S")) {

                                trace[j][i] = trace[j][h] + "," + trace[h + 1][i];

                                String parseStep = trace[j][i];

                                //LambdaCalculusExpression expr = combineLambda(first, second);
                                boolean isValid = true;
                                for (int k = 0; k < categories.size(); k++) {
                                    if (!parseStep.contains(k + "")) {
                                        isValid = false;
                                        break;
                                    }
                                }

                                if (isValid) {
                                    CCGDerivation parsedDerivation = new CCGDerivation();
                                    parsedDerivation.setEntries(tokens);

                                    if (combined.getLambda() != null) {
                                        parsedDerivation.setExpression(combined.getLambda());
                                        parsedDerivation.setParseStep(parseStep);
                                        parsedDerivation.setScore(d.getScore());
                                        parsedDerivation.setIsParsed(true);

                                        boolean exists = false;
//                                        for(Derivation previous : derivations){
//                                            String prevAsString = previous.toString();
//                                            String newOne =  parsedDerivation.toString();
//                                            
//                                            if(prevAsString.equalsIgnoreCase(newOne)){
//                                                exists = true;
//                                                break;
//                                            }
//                                        }
                                        if(!exists){
                                            derivations.add(parsedDerivation);
                                        }
                                        
                                    }
                                } else {//if the result is S but there are still some other entries left to combine like S/S
                                    if (parse[j][i] == null) {
                                        parse[j][i] = combined.clone();
                                        trace[j][i] = "(" + trace[j][h] + "," + trace[h + 1][i] + ")";
                                    }
                                }

                            } else {
                                if (parse[j][i] == null) {
                                    parse[j][i] = combined.clone();
                                    trace[j][i] = "(" + trace[j][h] + "," + trace[h + 1][i] + ")";
                                }
                            }
                        }
                    }
                }
            }
        }
        return derivations;
    }

    private String checkSyntax(String result) {
        if (!result.equals("")) {
            if (result.contains("/") || result.contains("\\")) {
                CCGCategory r = factory.construcCCGCategory(result);
                if (r == null) {
                    result = "(" + result + ")";
                }
            }
        }
        return result;
    }

    public CCGLexEntry combineCCG(CCGLexEntry f, CCGLexEntry s) {
        String result = "";
        CCGCategory cat1 = f.getSyntax();
        CCGCategory cat2 = s.getSyntax();
        String combinationRule = "";

        /*
         Combination Rule 1:
         X/Y Y => X
        
         Combination Rule 2:
         Y X\Y => X
        
         Combination Rule 3:
         Y\Z X\Y  => X\Z
        
         Combination Rule 4:
         X/Y Y/Z  => X/Z
        
         */
        boolean isCombined = false;

        if (cat1 instanceof CombinedCategory && cat2 instanceof AtomicCategory) {
            CombinedCategory first = (CombinedCategory) cat1;
            AtomicCategory second = (AtomicCategory) cat2;

            // X/Y Y => X
            if (first.getOp().equals(CombinedCategory.Operator.slash)) {
                if (first.getRight().toString().equals(second.toString())) {
                    result = first.getLeft().toString();

                    result = checkSyntax(result);

                    isCombined = true;
                    combinationRule = "1";

                    FunctionalApplication app = new FunctionalApplication();
                    app.setFunction(f.getLambda());
                    app.setArgument(s.getLambda());

                    CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), app);
                    return combined;
                }
            }
        }
        if (cat2 instanceof CombinedCategory && cat1 instanceof AtomicCategory) {
            AtomicCategory first = (AtomicCategory) cat1;
            CombinedCategory second = (CombinedCategory) cat2;

            // Y X\Y => X
            if (second.getOp().equals(CombinedCategory.Operator.backslash)) {
                if (second.getRight().toString().equals(first.toString())) {
                    result = second.getLeft().toString();
                    isCombined = true;
                    combinationRule = "2";

                    result = checkSyntax(result);

                    FunctionalApplication app = new FunctionalApplication();
                    app.setFunction(s.getLambda());
                    app.setArgument(f.getLambda());

                    CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), app);
                    return combined;
                }
            }
        }
        if (cat2 instanceof CombinedCategory && cat1 instanceof CombinedCategory) {
            CombinedCategory first = (CombinedCategory) cat1;
            CombinedCategory second = (CombinedCategory) cat2;

            // Y\Z X\Y  => X\Z
            if (first.getOp().equals(CombinedCategory.Operator.backslash) && second.getOp().equals(CombinedCategory.Operator.backslash)) {
                if (first.getLeft().toString().equals(second.getRight().toString())) {
                    result = second.getLeft().toString() + "\\" + first.getRight().toString();
                    isCombined = true;
                    combinationRule = "3";

                    result = checkSyntax(result);

                    Variable z = new Variable("z");

                    FunctionalApplication app = new FunctionalApplication();
                    app.setFunction(f.getLambda());
                    app.setArgument(z);

                    FunctionalApplication r = new FunctionalApplication();
                    r.setArgument(app);
                    r.setFunction(s.getLambda());

                    LambdaCalculus.LambdaAbstraction abs = new LambdaAbstraction();
                    abs.setBody(r);
                    abs.setVar(z);

                    CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), abs);
                    return combined;
                }
            }
            // X/Y Y/Z  => X/Z
            if (first.getOp().equals(CombinedCategory.Operator.slash) && second.getOp().equals(CombinedCategory.Operator.slash)) {
                if (first.getRight().toString().equals(second.getLeft().toString())) {
                    result = first.getLeft().toString() + "/" + second.getRight().toString();
                    isCombined = true;
                    combinationRule = "4";

                    result = checkSyntax(result);

                    Variable z = new Variable("z");

                    FunctionalApplication app = new FunctionalApplication();
                    app.setFunction(s.getLambda());
                    app.setArgument(z);

                    FunctionalApplication r = new FunctionalApplication();
                    r.setArgument(app);
                    r.setFunction(f.getLambda());

                    LambdaCalculus.LambdaAbstraction abs = new LambdaAbstraction();
                    abs.setBody(r);
                    abs.setVar(z);

                    CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), abs);

                    return combined;

                }
            }
            // X/Y Y => X
            if (first.getOp().equals(CombinedCategory.Operator.slash) && first.getRight().toString().equals(second.toString())) {
                isCombined = true;
                result = first.getLeft().toString();
                combinationRule = "1";

                result = checkSyntax(result);

                FunctionalApplication app = new FunctionalApplication();
                app.setFunction(f.getLambda());
                app.setArgument(s.getLambda());

                CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), app);
                return combined;
            }
            // Y X\Y => X
            if (second.getOp().equals(CombinedCategory.Operator.backslash) && second.getRight().toString().equals(first.toString())) {
                isCombined = true;
                result = second.getLeft().toString();
                combinationRule = "2";

                result = checkSyntax(result);

                FunctionalApplication app = new FunctionalApplication();
                app.setFunction(s.getLambda());
                app.setArgument(f.getLambda());

                CCGLexEntry combined = new CCGLexEntry(f.getLemma() + "=" + s.getLemma(), factory.construcCCGCategory(result), app);

                return combined;
            }

        }
        return null;
    }
}

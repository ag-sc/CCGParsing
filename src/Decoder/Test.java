/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Decoder;

import Database.DBManagement;
import Derivation.CCGDerivation;
import Derivation.Covering;
import Derivation.Derivation;
import Derivation.Interval;
import Factory.Factory;
import Feature.Feature;
import Grammar.CCGLexEntry;
import LambdaCalculus.Atom;
import LambdaCalculus.Constant;
import LambdaCalculus.ExistQuantifier;
import LambdaCalculus.Expression;
import Lemmatizer.Lemmatizer;
import Lemmatizer.StanfordLemmatizer;
import Model.CCGModelOnSQL;
import Parser.SyntacticParser;
import Reader.QALD.SPARQLParser;
import java.sql.DriverManager;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args) {

        Logging.Logger.setFilePATH("src/Reader/Files/logs/log" + 1 + ".txt");
        
        
        DBManagement dbProcessor = new DBManagement("sherzod", "123asd", "qa");
        
        
        Lemmatizer lemmatizer = new StanfordLemmatizer();
        SPARQLParser sparqlParser = new SPARQLParser();

        CCGModelOnSQL model = new CCGModelOnSQL(dbProcessor, lemmatizer);

        List<Feature> f = model.getEntriesforLexeme("country", -1);

        Decoder decoder = new StackDecoder(model, 15000, 3);

        String s = "are tree frogs a type of ";
        List<Covering> coverings = decoder.decode(s.split(" "), 28);
        
        
        
        for(Covering c : coverings){
            Logging.Logger.logText(c.toString()+"\n\n");
        }
        
        Logging.Logger.setFilePATH("src/Reader/Files/logs/log" + 2 + ".txt");

        SyntacticParser parser = new SyntacticParser();
        List<Derivation> derivations = parser.parse(coverings);
        for (Derivation d : derivations) {
            CCGDerivation derivation = (CCGDerivation) d;

            Expression e = derivation.getExpression();
            String query = sparqlParser.convertLogicalFormToQuery(e.reduce());
            System.out.println(query);
            Logging.Logger.logText(derivation.toString()+"\n\n");

        }

    }

}

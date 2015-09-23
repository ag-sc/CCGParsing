/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reader.QALD;

import LambdaCalculus.AggregationQuantifier;
import LambdaCalculus.Atom;
import LambdaCalculus.Constant;
import LambdaCalculus.CountingQuantifier;
import LambdaCalculus.ExistQuantifier;
import LambdaCalculus.Expression;
import LambdaCalculus.LambdaAbstraction;
import LambdaCalculus.MultiConnector;
import LambdaCalculus.Term;
import LambdaCalculus.Variable;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sherzod
 */
public class SPARQLParser {

    public SPARQLParser() {
    }

    private String returnVariable = "";

    public String getReturnVariable() {
        return returnVariable;
    }

    public void setReturnVariable(String returnVariable) {
        this.returnVariable = returnVariable;
    }

    public Expression convertQueryToLogicalForm(String queryString) {

        Expression f = null;

        Query query = QueryFactory.create(queryString);

        if (query.hasAggregators()) {
            return getCountingQuantifier(query);
        } else {
            if (query.hasOrderBy() && query.hasLimit() && query.hasOffset()) {
                return (Expression) getAggregationQuantifier(query);
            } else if (!query.hasLimit() && !query.hasOffset() && !query.isAskType()) {
                return getStandardQuantifier(query);
            } else if (!query.hasLimit() && !query.hasOffset() && query.isAskType()) {
                return getBooleanQuantifier(query);
            }
        }

        return f;
    }

    public boolean isValidCombination(Expression p) {
        boolean isValid = true;

        String query = convertLogicalFormToQuery(p);

        return isValid;
    }

    public String convertLogicalFormToQuery(Expression p) {
        try {
            String prefix = ""
                    + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                    + "PREFIX res: <http://dbpedia.org/resource/>\n"
                    + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                    + "";
            String query = "";

            if (p == null) {
                return query;
            }

            if (p instanceof LambdaAbstraction) {
                LambdaAbstraction abs = (LambdaAbstraction) p;

                if (abs.getBody() instanceof Atom || abs.getBody() instanceof MultiConnector) {
                    query += "SELECT DISTINCT ?" + abs.getVar().getValue() + " WHERE { \n";
                    query += getTriples(abs.getBody());
                    query += " }";
                }
                if (abs.getBody() instanceof CountingQuantifier) {
                    query += "SELECT COUNT (DISTINCT ?" + abs.getVar().getValue() + ") WHERE { \n";

                    CountingQuantifier c = (CountingQuantifier) abs.getBody();
                    query += getTriples(c.getCondition());
                    query += " }";
                }
                if (abs.getBody() instanceof AggregationQuantifier) {
                    query += "SELECT COUNT (DISTINCT ?" + abs.getVar().getValue() + ") WHERE { \n";
                    query += getTriples(abs.getBody());

                    AggregationQuantifier agg = (AggregationQuantifier) abs.getBody();

                    query += getTriples(agg.getCondition());
                    query += getTriples(agg.getAggregationExpression());
                    query += " }";

                    if (agg.getQuant().equals("argmax")) {
                        query += "ORDER BY DESC(" + agg.getAggregationVariable() + ") \n"
                                + "OFFSET 0 LIMIT 1\n";
                    }
                    if (agg.getQuant().equals("argmin")) {
                        query += "ORDER BY ASC(" + agg.getAggregationVariable() + ") \n"
                                + "OFFSET 0 LIMIT 1 \n";
                    }
                }

                setReturnVariable("?" + abs.getVar().getValue());
            }
            if (p instanceof ExistQuantifier) {
                ExistQuantifier e = (ExistQuantifier) p;
                String triples = getTriples(e.getCondition());
                if (!triples.equals("") && !triples.contains("?")) {
                    query += "ASK WHERE { \n";
                    query += triples;
                    query += " }";
                }

            }

            if(!query.equals("")){
                query = prefix+query;
            }
            return query;
        } catch (Exception e) {
//            System.err.println("Problem with converting logical form to SPARQL");
//            System.err.println(e.getMessage());
            return "";
        }

    }

    private Expression getCountingQuantifier(Query query) {
        if (query != null) {

            String returnVariable = "";

            List<ExprAggregator> aggregators = query.getAggregators();
            for (ExprAggregator a : aggregators) {
                returnVariable = a.getAggregator().getExpr().getVarName();
            }

            List<Atom> predicates = getPredicates(query);

            //initialize
            CountingQuantifier q = new CountingQuantifier();
            //add predicate(s)
            if (predicates.size() > 1) {
                MultiConnector m = new MultiConnector();
                for (Atom a : predicates) {
                    m.add(a);
                }

                q.setCondition(m);
            } else if (predicates.size() == 1) {
                q.setCondition(predicates.get(0));
            }

            //create LambdaAbstraction with return variable
            LambdaAbstraction abs = new LambdaAbstraction();
            abs.setVar(new Variable(returnVariable));
            abs.setBody(q);

            return abs;
        }
        return null;
    }

    private Expression getStandardQuantifier(Query query) {
        if (query != null) {

            String returnVariable = "";

            for (String s : query.getResultVars()) {
                returnVariable = s;
            }

            List<Atom> predicates = getPredicates(query);

            //initialize
            Expression body = null;
            //add predicate(s)
            if (predicates.size() > 1) {
                MultiConnector m = new MultiConnector();
                for (Atom a : predicates) {
                    m.add(a);
                }

                body = m;
            } else if (predicates.size() == 1) {
                body = predicates.get(0);
            }

            LambdaAbstraction abs = new LambdaAbstraction();
            abs.setVar(new Variable(returnVariable));
            abs.setBody(body);

            return abs;
        }

        return null;
    }

    private Expression getBooleanQuantifier(Query query) {
        if (query != null) {

            String returnVariable = "";

            List<ExprAggregator> aggregators = query.getAggregators();
            for (ExprAggregator a : aggregators) {
                returnVariable = a.getAggregator().getExpr().getVarName();
            }

            List<Atom> predicates = getPredicates(query);

            //initialize
            ExistQuantifier q = new ExistQuantifier();
            //add predicate(s)
            if (predicates.size() > 1) {
                MultiConnector m = new MultiConnector();
                for (Atom a : predicates) {
                    m.add(a);
                }

                q.setCondition(m);
            } else if (predicates.size() == 1) {
                q.setCondition(predicates.get(0));
            }

            if (returnVariable.equals("")) {
                return q;
            }
            //create LambdaAbstraction with return variable
            LambdaAbstraction abs = new LambdaAbstraction();
            abs.setVar(new Variable(returnVariable));
            abs.setBody(q);

            return abs;
        }
        return null;
    }

    private Expression getAggregationQuantifier(Query query) {
        AggregationQuantifier q = null;

        if (query != null) {
            String returnVariable = "";

            for (String s : query.getResultVars()) {
                returnVariable = s;
            }

            String aggregationVariable = "";
            String quantor = "";
            List<SortCondition> orderBy = query.getOrderBy();

            for (SortCondition c : orderBy) {
                if (c.direction == -1 || c.direction == -2) {
                    quantor = "max";
                }
                if (c.direction == 1) {
                    quantor = "min";
                }
                aggregationVariable = c.expression.toString();
                aggregationVariable = aggregationVariable.replace("?", "");
            }

            List<Atom> triples = getPredicates(query, aggregationVariable, false);
            List<Atom> aggregationFunction = getPredicates(query, aggregationVariable, true);

            q = new AggregationQuantifier();

            //set condition
            if (triples.size() > 1) {
                MultiConnector m = new MultiConnector();
                for (Atom a : triples) {
                    m.add(a);
                }
                q.setCondition(m);
            } else if (triples.size() == 1) {
                q.setCondition(triples.get(0));
            }

            //set aggregation function
            if (aggregationFunction.size() > 1) {
                MultiConnector m = new MultiConnector();
                for (Atom a : aggregationFunction) {
                    m.add(a);
                }
                q.setAggregationExpression(m);
            } else if (aggregationFunction.size() == 1) {
                q.setAggregationExpression(aggregationFunction.get(0));
            }

            //set aggregation var
            q.setAggregationVariable(new Variable(aggregationVariable));

            //set quantor
            if (quantor.equals("max")) {
                q.setQuant(AggregationQuantifier.Quantor.argmax);
            } else if (quantor.equals("min")) {
                q.setQuant(AggregationQuantifier.Quantor.argmin);
            }

            LambdaAbstraction abs = new LambdaAbstraction();
            abs.setVar(new Variable(returnVariable));
            abs.setBody(q);

            return abs;
        }
        return null;
    }

    /*
     * @return all triples as Atoms
     * @param SPARQL query
     */
    private List<Atom> getPredicates(Query query) {
        List<Atom> atoms = new ArrayList<>();

        ElementGroup body = (ElementGroup) query.getQueryPattern();

        for (Element e : body.getElements()) {
            ElementPathBlock block = (ElementPathBlock) e;

            Iterator<TriplePath> iterator = block.patternElts();

            while (iterator.hasNext()) {
                TriplePath t = iterator.next();

                if (t.getPredicate().isURI()) {
                    Atom a = new Atom();
                    a.setPredicate(t.getPredicate().getURI());

                    if (t.getSubject().isVariable()) {
                        a.getArguments().add(new Variable(t.getSubject().toString().replace("?", "")));
                    }
                    if (t.getSubject().isURI()) {
                        a.getArguments().add(new Constant(t.getSubject().getURI()));
                    }
                    if (t.getSubject().isLiteral()) {
                        String l = t.getSubject().getLiteral().toString(true);
                        a.getArguments().add(new Constant(l));
                    }

                    if (t.getObject().isVariable()) {
                        a.getArguments().add(new Variable(t.getObject().toString().replace("?", "")));
                    }
                    if (t.getObject().isURI()) {
                        a.getArguments().add(new Constant(t.getObject().getURI()));
                    }
                    if (t.getObject().isLiteral()) {
                        String l = t.getObject().getLiteral().toString(true);
                        a.getArguments().add(new Constant(l));
                    }

                    atoms.add(replaceNameSpaces(a));
                }
            }
        }

        return atoms;
    }
    
    private Atom replaceNameSpaces(Atom a){
        String p = a.getPredicate();
        
        p = p.replace("http://dbpedia.org/ontology/", "dbo:");
        p = p.replace("http://dbpedia.org/property/", "dbp:");
        p = p.replace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
        
        a.setPredicate(p);
        
        for(Term t : a.getArguments()){
            if(t instanceof Constant){
                Constant constant = (Constant) t;
                String c = constant.getValue();
                
                c = c.replace("http://dbpedia.org/resource/", "res:");
                c = c.replace("http://www.w3.org/2002/07/owl#", "owl:");
                
                constant.setValue(c);
            }
        }
        
        return a;
    }

    /*
     * @return triples except triple that includes given variable 
     * @param SPARQL query
     * @param String variable
     */
    private List<Atom> getPredicates(Query query, String variable, boolean included) {
        List<Atom> atoms = new ArrayList<>();

        ElementGroup body = (ElementGroup) query.getQueryPattern();

        for (Element e : body.getElements()) {
            ElementPathBlock block = (ElementPathBlock) e;

            Iterator<TriplePath> iterator = block.patternElts();

            while (iterator.hasNext()) {
                TriplePath t = iterator.next();

                boolean found = false;
                if (t.getPredicate().isURI()) {
                    Atom a = new Atom();
                    a.setPredicate(t.getPredicate().getURI());

                    if (t.getSubject().isVariable()) {
                        String v = t.getSubject().toString().replace("?", "");
                        a.getArguments().add(new Variable(v));
                        if (v.equals(variable)) {
                            found = true;
                        }
                    }
                    if (t.getSubject().isURI()) {
                        a.getArguments().add(new Constant(t.getSubject().getURI()));
                        if (t.getSubject().getURI().equals(variable)) {
                            found = true;
                        }
                    }

                    if (t.getObject().isVariable()) {
                        String v = t.getObject().toString().replace("?", "");
                        a.getArguments().add(new Variable(v));
                        if (v.equals(variable)) {
                            found = true;
                        }
                    }
                    if (t.getObject().isURI()) {
                        a.getArguments().add(new Constant(t.getObject().getURI()));
                        if (t.getObject().getURI().equals(variable)) {
                            found = true;
                        }
                    }

                    if (included && found) {
                        atoms.add(a);
                    }
                    if (!included && !found) {
                        atoms.add(a);
                    }
                }
            }
        }

        return atoms;
    }

    /*
     * @param list of atoms
     * @return triples
     */
    private String getTriples(Expression p) {
        String triples = "";

        if (p instanceof MultiConnector) {
            MultiConnector m = (MultiConnector) p;

            for (Expression pred : m.getPredicates()) {
                Atom predicate = (Atom) pred;

                if (!predicate.getPredicate().equals("NULL")) {
                    String triple = "";
                    for (Term t : predicate.getArguments()) {

                        if (t instanceof Constant) {
                            Constant c = (Constant) t;

                            if (c.getValue().contains("@en") || c.getValue().contains("\"")) {
                                if (predicate.getArguments().indexOf(t) == 0) {
                                    triple = "" + c.getValue() + " <" + predicate.getPredicate() + "> ";
                                }
                                if (predicate.getArguments().indexOf(t) == 1) {
                                    triple += "" + c.getValue() + " .\n";
                                }
                            } else {
                                if (predicate.getArguments().indexOf(t) == 0) {
                                    triple = "<" + c.getValue() + "> <" + predicate.getPredicate() + "> ";
                                }
                                if (predicate.getArguments().indexOf(t) == 1) {
                                    triple += "<" + c.getValue() + "> .\n";
                                }
                            }

                        }
                        if (t instanceof Variable) {
                            Variable v = (Variable) t;
                            if (predicate.getArguments().indexOf(t) == 0) {
                                triple = "?" + v.getValue() + " <" + predicate.getPredicate() + "> ";
                            }
                            if (predicate.getArguments().indexOf(t) == 1) {
                                triple += "?" + v.getValue() + " .\n";
                            }
                        }
                    }
                    if (triples.contains(triple)) {
                        return "NOT VALID QUERY";
                    }
                    triples += "\t" + triple;
                }

            }
        }

        if (p instanceof Atom) {

            Atom predicate = (Atom) p;
            if (!predicate.getPredicate().equals("NULL")) {
                String triple = "";
                for (Term t : predicate.getArguments()) {

                    if (t instanceof Constant) {
                        Constant c = (Constant) t;
                        if (c.getValue().contains("@en") || c.getValue().contains("\"")) {
                            if (predicate.getArguments().indexOf(t) == 0) {
                                triple = "" + c.getValue() + " <" + predicate.getPredicate() + "> ";
                            }
                            if (predicate.getArguments().indexOf(t) == 1) {
                                triple += "" + c.getValue() + " .\n";
                            }
                        } else {
                            if (predicate.getArguments().indexOf(t) == 0) {
                                triple = "<" + c.getValue() + "> <" + predicate.getPredicate() + "> ";
                            }
                            if (predicate.getArguments().indexOf(t) == 1) {
                                triple += "<" + c.getValue() + "> .\n";
                            }
                        }
                    }
                    if (t instanceof Variable) {
                        Variable v = (Variable) t;
                        if (predicate.getArguments().indexOf(t) == 0) {
                            triple = "?" + v.getValue() + " <" + predicate.getPredicate() + "> ";
                        }
                        if (predicate.getArguments().indexOf(t) == 1) {
                            triple += "?" + v.getValue() + " .\n";
                        }
                    }
                }

                triples += "\t" + triple;
            }

        }

        return triples;
    }
}

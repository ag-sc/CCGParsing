/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 *
 * @author sherzod
 */
public class TestJena {

    public static void main(String[] args) {
        String sparqlQueryString1 = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "SELECT DISTINCT ?s ?l WHERE { \n"
                + "	?s a owl:Class.\n"
                + "?s rdfs:label ?l.\n"
                + "FILTER(langMatches(lang(?l), \"EN\"))\n"
                + " }";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet rs = qexec.execSelect();

        for (; rs.hasNext();) {
            QuerySolution rb = rs.nextSolution();

            // Get title - variable names do not include the '?' (or '$')
            RDFNode x = rb.get("s");

            RDFNode x2 = rb.get("l");

            // Check the type of the result value
            if(x.toString().contains("ontology"))
                System.out.println(x.toString().replace("#", "http://dbpedia.org/ontology/") + "\t"+ x2.toString().replace("@en", ""));

        }

        qexec.close();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

/**
 *
 * @author sherzod
 */
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import java.util.ArrayList;

public class QueryDBpedia {

    static public void main(String... argv) {
        String queryStr = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "SELECT DISTINCT ?s ?l WHERE { \n"
                + "	?s rdf:type owl:ObjectProperty.\n"
                + "?s rdfs:label ?l.\n"
                + "FILTER(langMatches(lang(?l), \"EN\"))\n"
                + " }";
        QueryDBpedia db = new QueryDBpedia();
        ArrayList<String> resl = db.getAnswers(queryStr, "s");

        int a = 1;
    }

    public ArrayList<String> getAnswers(String queryString, String returnVariable) {
        ArrayList<String> result = new ArrayList<>();
        try {

            Query query = QueryFactory.create(queryString);

            // Remote execution.
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP) qexec).addParam("timeout", "30000");

            // Execute.
            if (queryString.contains("ASK WHERE")) {
                boolean r = qexec.execAsk();
                result.add(r + "");

                return result;
            }

            ResultSet rs = qexec.execSelect();

            for (; rs.hasNext();) {
                QuerySolution rb = rs.nextSolution();

                // Get title - variable names do not include the '?' (or '$')
                RDFNode x = rb.get(returnVariable);

                // Check the type of the result value
                result.add(x.toString());

            }
            //ResultSetFormatter.out(System.out, rs, query);
            qexec.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

}

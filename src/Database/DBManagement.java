/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import static Database.Test.READ_OBJECT_SQL;
import static Database.Test.WRITE_OBJECT_SQL;
import static Database.Test.excutePost;
import static Database.Test.readJavaObject;
import Derivation.Covering;
import Factory.Factory;
import Feature.CCGFeature;
import Feature.Feature;
import Grammar.CCGLexEntry;
import LambdaCalculus.Expression;
import com.mysql.jdbc.DatabaseMetaData;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author sherzod
 */
public class DBManagement {

    private Factory factory;
    private com.mysql.jdbc.Connection conn;

    public DBManagement(String username, String password, String database) {
        factory = new Factory();
        conn = getConnection(username, password, database);
        if(!lexiconTableExists()){
            createTable();
        }
    }

    public com.mysql.jdbc.Connection getConnection(String username, String password, String database) {
        try {
            String driver = "org.gjt.mm.mysql.Driver";
            String url = "jdbc:mysql://localhost/"+database;
            //String username = "root";
            //String password = "";
            Class.forName(driver);
            com.mysql.jdbc.Connection conn = (com.mysql.jdbc.Connection) DriverManager.getConnection(url, username, password);
            System.out.println("DB Connected");
            return conn;
        } catch (Exception e) {

        }
        return null;
    }

    public boolean lexiconTableExists() {
        try {

            DatabaseMetaData dbmd = (DatabaseMetaData) conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = dbmd.getTables(null, null, "%", types);
            while (rs.next()) {
                if (rs.getString("TABLE_NAME").equals("lexicon")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTable() {
        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE `lexicon` (\n"
                    + "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n"
                    + "  `lemma` varchar(90),\n"
                    + "  `semantics` longtext,\n"
                    + "  `syntax` longtext,\n"
                    + "  `score` double DEFAULT NULL,\n"
                    + "  `isUsed` varchar(45) DEFAULT NULL,\n"
                    + "  `instanceId` varchar(45) DEFAULT NULL,\n"
                    + "  PRIMARY KEY (`ID`),\n"
                    + "  KEY `lemma` (`lemma`)\n"
                    + ") ENGINE=InnoDB AUTO_INCREMENT=10540 DEFAULT CHARSET=latin1;";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public long insertFeature(CCGFeature f) throws Exception {

        String query = "INSERT INTO lexicon(lemma, semantics, syntax, score, isUsed, instanceId) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        // set input parameters
        pstmt.setString(1, f.getWord());

        if (f.getLambda() != null) {
            pstmt.setString(2, f.getLambda().toString());
        } else {
            pstmt.setString(2, "");
        }

        pstmt.setString(3, f.getCategory().toString());
        pstmt.setDouble(4, f.getScore());
        pstmt.setString(5, f.isUsed() + "");
        pstmt.setString(6, f.getInstanceId() + "");
        pstmt.executeUpdate();

        // get the generated key for the id
        ResultSet rs = pstmt.getGeneratedKeys();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt(1);
        }

        rs.close();
        pstmt.close();
        return id;
    }

    public void updateFeatureScore(CCGFeature f) throws Exception {

        String query = "UPDATE lexicon SET score=? where lemma=? and semantics=? and syntax=? and isUsed=? and instanceId=? ";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setDouble(1, f.getScore());
        pstmt.setString(2, f.getWord());

        if (f.getLambda() != null) {
            pstmt.setString(3, f.getLambda().toString());
        } else {
            pstmt.setString(3, "");
        }

        pstmt.setString(4, f.getCategory().toString());
        pstmt.setString(5, f.isUsed() + "");
        pstmt.setString(6, f.getInstanceId() + "");
        pstmt.executeUpdate();

        // get the generated key for the id
    }

    public void updateFeatureIsUsed(CCGFeature f) throws Exception {

        String query = "UPDATE lexicon SET isUsed=? where lemma=? and semantics=? and syntax=? and isUsed=? ";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setString(1, f.isUsed() + "");
        pstmt.setString(2, f.getWord());

        if (f.getLambda() != null) {
            pstmt.setString(3, f.getLambda().toString());
        } else {
            pstmt.setString(3, "");
        }

        pstmt.setString(4, f.getCategory().toString());
        pstmt.setString(5, "false");
        pstmt.executeUpdate();

        // get the generated key for the id
    }

    public void updateAllFeaturesIsUsed(String isUsed) throws Exception {

        String query = "UPDATE lexicon SET isUsed=? ";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setString(1, isUsed);
        pstmt.executeUpdate();

    }//HERE instanceId <> '-1'

    public void updateAllInducedFeaturesScore(double score) throws Exception {

        String query = "UPDATE lexicon SET score=? WHERE instanceId <> '-1'";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setDouble(1, score);
        pstmt.executeUpdate();

    }//HERE instanceId <> '-1'

    public void updateDomainIndependentFeaturesScore(double score) throws Exception {

        String query = "UPDATE lexicon SET score=? WHERE instanceId = '-1'";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setDouble(1, score);
        pstmt.executeUpdate();

    }//HERE instanceId <> '-1'

    public void updateAllFeaturesScore(double score) throws Exception {

        String query = "UPDATE lexicon SET score=? ";
        PreparedStatement pstmt = conn.prepareStatement(query);

        // set input parameters
        pstmt.setDouble(1, score);
        pstmt.executeUpdate();

    }

    public void deleteAllFeatures() throws Exception {

        String query = "TRUNCATE TABLE lexicon ";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.executeUpdate();

    }

    public void deleteAllInducedFeatures() throws Exception {

        String query = "DELETE from lexicon where instanceId <> '-1'";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.executeUpdate();

    }

    public List<Feature> getFeaturesWithLemma(String lemma) throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT semantics, syntax, score, isUsed, instanceId FROM lexicon WHERE lemma = ?");
        pstmt.setString(1, lemma);
        ResultSet rs = pstmt.executeQuery();

        List<Feature> features = new ArrayList<>();

        while (rs.next()) {
            CCGFeature f = new CCGFeature();

            String semantics = rs.getString(1);

            Expression expression = factory.constructLambdaCalculusExpression(semantics);

            String ccg = rs.getString(2);
            double score = rs.getDouble(3);
            String isUsed = rs.getString(4);
            String instanceId = rs.getString(5);

            f.setWord(lemma);
            f.setLambda(expression);
            f.setCategory(factory.construcCCGCategory(ccg));
            f.setScore(score);
            if (isUsed.equals("false")) {
                f.setIsUsed(false);
            } else {
                f.setIsUsed(true);
            }
            f.setInstanceId(Integer.parseInt(instanceId));

            features.add(f);
        }

        rs.close();
        pstmt.close();
        return features;
    }

    public List<CCGFeature> getInducedFeatures() throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT semantics, syntax, score, isUsed, instanceId, lemma FROM lexicon WHERE instanceId <> '-1' AND isUsed = 'true' AND score > 0");

        ResultSet rs = pstmt.executeQuery();

        List<CCGFeature> features = new ArrayList<>();

        while (rs.next()) {
            CCGFeature f = new CCGFeature();

            String semantics = rs.getString(1);

            Expression expression = factory.constructLambdaCalculusExpression(semantics);

            String ccg = rs.getString(2);
            double score = rs.getDouble(3);
            String isUsed = rs.getString(4);
            String instanceId = rs.getString(5);
            String lemma = rs.getString(6);

            f.setWord(lemma);
            f.setLambda(expression);
            f.setCategory(factory.construcCCGCategory(ccg));
            f.setScore(score);
            if (isUsed.equals("false")) {
                f.setIsUsed(false);
            } else {
                f.setIsUsed(true);
            }
            f.setInstanceId(Integer.parseInt(instanceId));

            features.add(f);
        }

        rs.close();
        pstmt.close();
        return features;
    }

    public List<Feature> getFeaturesWithLemmaInstanceId(String lemma, String instanceId) throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT semantics, syntax, score, isUsed FROM lexicon WHERE lemma = ? and instanceId= ? ");
        pstmt.setString(1, lemma);
        pstmt.setString(2, instanceId);
        ResultSet rs = pstmt.executeQuery();

        List<Feature> features = new ArrayList<>();

        while (rs.next()) {
            CCGFeature f = new CCGFeature();

            String semantics = rs.getString(1);
            Expression expression = factory.constructLambdaCalculusExpression(semantics);
            String ccg = rs.getString(2);
            double score = rs.getDouble(3);
            String isUsed = rs.getString(4);

            f.setWord(lemma);
            f.setLambda(expression);
            f.setCategory(factory.construcCCGCategory(ccg));
            f.setScore(score);
            if (isUsed.equals("false")) {
                f.setIsUsed(false);
            } else {
                f.setIsUsed(true);
            }
            f.setInstanceId(Integer.parseInt(instanceId));

            features.add(f);
        }

        rs.close();
        pstmt.close();
        return features;
    }

    public List<CCGFeature> getFeaturesWithInstanceId(String instanceId) throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT semantics, syntax, score, isUsed, lemma FROM lexicon WHERE instanceId= ? ");
        pstmt.setString(1, instanceId);
        ResultSet rs = pstmt.executeQuery();

        List<CCGFeature> features = new ArrayList<>();

        while (rs.next()) {
            CCGFeature f = new CCGFeature();

            String semantics = rs.getString(1);
            Expression expression = factory.constructLambdaCalculusExpression(semantics);
            String ccg = rs.getString(2);
            double score = rs.getDouble(3);
            String isUsed = rs.getString(4);
            String lemma = rs.getString(5);

            f.setWord(lemma);
            f.setLambda(expression);
            f.setCategory(factory.construcCCGCategory(ccg));
            f.setScore(score);
            if (isUsed.equals("false")) {
                f.setIsUsed(false);
            } else {
                f.setIsUsed(true);
            }
            f.setInstanceId(Integer.parseInt(instanceId));

            features.add(f);
        }

        rs.close();
        pstmt.close();
        return features;
    }

    public int getAllInducedFeaturesSize(String instanceId) throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(semantics) FROM lexicon WHERE instanceId <> ? ");
        pstmt.setString(1, instanceId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String count = rs.getString(1);
            return Integer.parseInt(count);
        }

        rs.close();
        pstmt.close();
        return 0;
    }

    public List<Feature> getFeaturesWithLemmaIsUsed(String lemma, String isUsed) throws Exception {

        PreparedStatement pstmt = conn.prepareStatement("SELECT semantics, syntax, score, instanceId FROM lexicon WHERE lemma = ? and isUsed= ? ");
        pstmt.setString(1, lemma);
        pstmt.setString(2, isUsed);
        ResultSet rs = pstmt.executeQuery();

        List<Feature> features = new ArrayList<>();

        while (rs.next()) {
            CCGFeature f = new CCGFeature();

            String semantics = rs.getString(1);
            Expression expression = factory.constructLambdaCalculusExpression(semantics);
            String ccg = rs.getString(2);
            double score = rs.getDouble(3);
            String instanceId = rs.getString(4);

            f.setWord(lemma);
            f.setLambda(expression);
            f.setCategory(factory.construcCCGCategory(ccg));
            f.setScore(score);
            if (isUsed.equals("false")) {
                f.setIsUsed(false);
            } else {
                f.setIsUsed(true);
            }
            f.setInstanceId(Integer.parseInt(instanceId));

            features.add(f);
        }

        rs.close();
        pstmt.close();
        return features;
    }

    /*
     * @param postag (noun, adjective, verb, adverb)
     * @return list of postags (nouns, adjectives, verbs, adverbs
     */
    public List<String> getPOSTags(String tag) {
        List<String> elements = null;

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elementPredicateObject = q1.getSubject("http://www.lexinfo.net/ontology/2.0/lexinfo#partOfSpeech", "http://www.lexinfo.net/ontology/2.0/lexinfo#" + tag);
        for (Element e : elementPredicateObject) {
            if (elements == null) {
                elements = new ArrayList<>();
            }

            String n = e.getSubject();
            n = n.replace("http://kaiko.getalp.org/dbnary/eng/", "");
            n = n.substring(0, n.indexOf("__"));
            n = n.replace("_", " ");
            elements.add(n);
        }

        return elements;
    }

    public String getComment(String tag) {
        String comment = "";

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elementPredicateObject = q1.getObject("http://kaiko.getalp.org/dbnary/eng/" + tag, "http://www.w3.org/2000/01/rdf-schema#comment");
        for (Element e : elementPredicateObject) {
            comment = e.getObject();
            break;
        }

        return comment;
    }

    /*
     *@return list of adjective with comparative and superlative
     */
    public List<String> getAdjectivesWithComparativeAndSuperlative() {
        List<String> elements = null;

        List<String> adjectives = getPOSTags("adjective");
//        adjectives.clear();
//        adjectives.add("highest");
//        adjectives.add("cuter");
//        adjectives.add("cute");
        for (String superlative : adjectives) {
            try {

                if (superlative.length() > 3) {
                    String baseForm = superlative.substring(0, superlative.length() - 3);
                    String ending = superlative.substring(superlative.length() - 3);
                    String adjective = "";

                    if (ending.equals("est")) {

                        if (baseForm.substring(baseForm.length() - 1).equals("i")) {
                            adjective = baseForm.substring(0, baseForm.length() - 1) + "y";

                            baseForm += "er";

                            //System.out.println(adjective + "\t" + baseForm + "\t" + superlative);
                        } else {
                            adjective = baseForm;
                            baseForm += "er";

                            //System.out.println(adjective + "\t" + baseForm + "\t" + superlative);
                        }

                        String webpage = excutePost("http://en.wiktionary.org/wiki/" + superlative, "");

                        if (webpage != null) {
                            if (webpage.contains("superlative")) {
                                if (webpage.contains("superlative</a> form of <span class")) {

                                    String b = webpage.substring(webpage.indexOf("superlative</a> form of <span class="));
                                    String a = b.substring(b.indexOf("<a href="), b.indexOf("</a></i></span>"));
                                    a = a.substring(a.indexOf(">") + 1);

                                    String c = excutePost("http://en.wiktionary.org/wiki/" + baseForm, "");
                                    if (c.contains("comparative</a> form of")) {
                                        if (adjectives.contains(a)) {
                                            if (elements == null) {
                                                elements = new ArrayList<>();
                                            }
                                            elements.add(a + "\t" + baseForm + "\t" + superlative);
                                            //System.out.println(a + "\t" + baseForm + "\t" + superlative);
                                        }
                                    }
                                } else {

                                    String c = excutePost("http://en.wiktionary.org/wiki/" + baseForm, "");
                                    if (c != null && c.contains("comparative</a> form of")) {
                                        System.err.println(adjective + "\t" + baseForm + "\t" + superlative);
//                                if (adjectives.contains(adjective)) {
//                                    if (elements == null) {
//                                        elements = new ArrayList<>();
//                                    }
//                                    //elements.add(adjective + "\t" + baseForm + "\t" + superlative);
//                                    System.err.println(adjective + "\t" + baseForm + "\t" + superlative);
//                                } else {
//                                    System.err.println("ERROR \t" + adjective + "\t" + baseForm + "\t" + superlative);
//                                    //System.out.println(elements.size());
//                                }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                System.err.println(ex.getMessage() + " " + superlative);
            }
        }
        return elements;
    }

    public List<String> getAdjectivesWithComparativeAndSuperlative2() {
        List<String> elements = new ArrayList<>();

        List<String> adjectives = getPOSTags("adjective");
//        adjectives.clear();
//        adjectives.add("alive and kicking");
//        adjectives.add("cuter");
//        adjectives.add("cute");
//        adjectives.add("absolute");
        for (String a : adjectives) {
            try {

                if (a.length() > 4) {
                    String endingEst = a.substring(a.length() - 3);
                    String endingEr = a.substring(a.length() - 2);
                    String ending = a.substring(a.length() - 1);
                    String superlative = "";
                    String comparative = "";
                    if (!endingEr.equals("er") || !endingEst.equals("est")) {
                        if (!a.contains(" ")) {

                            switch (ending) {
                                case "y":
                                    superlative = a.substring(0, a.length() - 1) + "iest";
                                    comparative = a.substring(0, a.length() - 1) + "ier";
                                    break;
                                case "e":
                                    superlative = a.substring(0, a.length() - 1) + "est";
                                    comparative = a.substring(0, a.length() - 1) + "er";
                                    break;
                                default:
                                    superlative = a + "est";
                                    comparative = a + "er";
                                    break;
                            }

                            String s = excutePost("http://en.wiktionary.org/wiki/" + superlative, "");
                            if (s != null) {
                                if (s.contains("superlative")) {
                                    String c = excutePost("http://en.wiktionary.org/wiki/" + comparative, "");
                                    if (c != null) {
                                        if (c.contains("comparative")) {
                                            elements.add(a + "\t" + comparative + "\t" + superlative);
                                            System.out.println(a + "\t" + comparative + "\t" + superlative);
                                        }
                                    }

                                }
                            } else {
                                System.err.println(a + "\t" + comparative + "\t" + superlative);
                            }

//                        if (ending.equals("a") || ending.equals("i") || ending.equals("o") || ending.equals("u")) {
//                            System.err.println(a);
//                        }
                        }

                    }
                }

            } catch (Exception ex) {
                System.err.println(ex.getMessage() + " " + a);
            }
        }
        return elements;
    }

    public List<String> getAdjectivesWithComparativeAndSuperlative3() {
        List<String> elements = new ArrayList<>();

        List<String> adjectives = getPOSTags("adjective");
        adjectives.clear();
//        adjectives.add("Greekest");
//        adjectives.add("happiest");
        adjectives.add("cutest");
//        adjectives.add("absolute");
        for (String a : adjectives) {
            try {

                String endingEst = a.substring(a.length() - 3);
                if (a.contains("iest") && a.length() > 4) {
                    endingEst = a.substring(a.length() - 4);
                }

                String adjective = "";
                String alternative = "";
                String superlative = "";
                String comparative = "";
                if (endingEst.contains("est")) {
                    if (!a.contains(" ")) {
                        switch (endingEst) {
                            case "iest":
                                adjective = a;
                                adjective = adjective.replace(adjective.substring(adjective.length() - 4), "y");
                                comparative = adjective.substring(0, adjective.length() - 1) + "ier";
                                superlative = a;
                                break;
                            case "est":
                                adjective = a;
                                adjective = adjective.substring(0, adjective.length() - 3);
                                alternative = a.substring(0, a.length() - 2);
                                comparative = a.substring(0, a.length() - 3) + "er";
                                superlative = a;
//                                if (comparative.substring(comparative.length() - 3).equals("eer")) {
//                                    comparative = comparative.substring(0, comparative.length() - 3) + "er";
//
//                                }
                                break;
                        }

                        String s = excutePost("http://en.wiktionary.org/wiki/" + superlative, "");
                        if (s != null) {
                            if (s.contains("superlative")) {
                                String c = excutePost("http://en.wiktionary.org/wiki/" + comparative, "");
                                if (c.contains("comparative")) {
                                    if (adjective.contains(adjective)) {
                                        elements.add(adjective + "\t" + comparative + "\t" + superlative);
                                        System.out.println(adjective + "\t" + comparative + "\t" + superlative);
                                    } else if (adjective.contains(alternative)) {
                                        elements.add(alternative + "\t" + comparative + "\t" + superlative);
                                        System.out.println(alternative + "\t" + comparative + "\t" + superlative);
                                    }

                                }
                            }
                        } else {
                            System.err.println(a);
                        }
                    }
                }

            } catch (Exception ex) {
                //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return elements;
    }

    public String getRDFSLabel(String uri) {
        String comment = "";

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elementPredicateObject = q1.getObject(uri, "http://www.w3.org/2000/01/rdf-schema#label");
        for (Element e : elementPredicateObject) {
            comment = e.getObject();

            if (comment.contains("@en")) {
                comment = comment.replace("\"", "");
                comment = comment.replace("@en", "");
                comment = comment.toLowerCase();

                break;
            }

        }

        return comment;
    }

    public List<String> getDBpediaCategories() {
        List<String> elements = null;

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elementPredicateObject = q1.getSubject("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#Class");
        for (Element e : elementPredicateObject) {
            if (elements == null) {
                elements = new ArrayList<>();
            }

            String n = e.getSubject();
            elements.add(n);
        }

        return elements;
    }

    public List<String> getDBpediaObjectProperties() {
        List<String> elements = null;

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elementPredicateObject = q1.getSubject("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#ObjectProperty");
        for (Element e : elementPredicateObject) {
            if (elements == null) {
                elements = new ArrayList<>();
            }

            String n = e.getSubject();
            elements.add(n);
        }
        elementPredicateObject = q1.getSubject("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#DatatypeProperty");
        for (Element e : elementPredicateObject) {
            if (elements == null) {
                elements = new ArrayList<>();
            }

            String n = e.getSubject();
            elements.add(n);
        }
//        elementPredicateObject = q1.getSubject("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
//        for (Element e : elementPredicateObject) {
//            if (elements == null) {
//                elements = new ArrayList<>();
//            }
//
//            String n = e.getSubject();
//            elements.add(n);
//        }

        return elements;
    }

    public List<String> getDBpediaPageLinks(String s) {

        List<String> result = new ArrayList<>();

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        List<Element> elements = q1.getObject(s, "http://dbpedia.org/ontology/wikiPageWikiLink");
        if (!elements.isEmpty()) {
            for (Element e : elements) {
                if (e.getSubject().contains("dbpedia.org/resource/")) {
                    result.add(e.getSubject());
                }
            }
        }

        return result;
    }

    public List<String> getDBpediaResources(String s) {

        List<String> result = new ArrayList<>();

        QueryDataProcessor q1 = new QueryDataProcessor(true);

        ArrayList<String> predicates = new ArrayList<>();
        predicates.add("http://www.w3.org/2000/01/rdf-schema#label");
        predicates.add("http://dbpedia.org/ontology/alias");
        predicates.add("http://dbpedia.org/property/alias");
        predicates.add("http://xmlns.com/foaf/0.1/name");
        predicates.add("http://xmlns.com/foaf/0.1/givenName");
        predicates.add("http://xmlns.com/foaf/0.1/surname");
        predicates.add("http://dbpedia.org/property/commonName");
        predicates.add("http://dbpedia.org/property/fullName");
        predicates.add("http://dbpedia.org/property/name");
        predicates.add("http://dbpedia.org/ontology/demonym");

        for (String p : predicates) {

            List<Element> elements = q1.getSubject(p, "\"" + s + "\"@en");
            if (!elements.isEmpty()) {
                for (Element e : elements) {
                    //System.out.println(s+" "+e.getSubject());
//                    boolean a = checkTypeOfResource(e.getSubject());
                    if (e.getSubject().contains("dbpedia.org/resource/")) {
                        result.add(e.getSubject());
                    }

//                    if (a) {
//                        result.add(e.getSubject());
//                    }
                }
            }
        }

        return result;
    }

    private boolean checkTypeOfResource(String uri) {

        QueryDataProcessor q1 = new QueryDataProcessor(true);
        List<Element> elements = q1.getObject(uri, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        if (!elements.isEmpty()) {
            for (Element e : elements) {
                if (e.getObject().contains("http://dbpedia.org/ontology/")) {
                    return true;
                }
                if (e.getObject().contains("http://xmlns.com/foaf/0.1/")) {
                    return true;
                }
            }
        }

        elements = q1.getObject(uri, "http://dbpedia.org/ontology/wikiPageRedirects");
        if (!elements.isEmpty()) {
            for (Element e : elements) {
                List<Element> elements2 = q1.getObject(e.getObject(), "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                if (!elements2.isEmpty()) {
                    for (Element e2 : elements2) {
                        if (e2.getObject().contains("http://dbpedia.org/ontology/")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static String excutePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", ""
                    + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response    
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            //e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

//    public static void main(String[] args) throws ClassNotFoundException, IOException {
//        // load the sqlite-JDBC driver using the current class loader
//        Class.forName("org.sqlite.JDBC");
//
//        Connection connection = null;
//        try {
//            // create a database connection
//            connection = getConnection();
//            testTables(connection);
//            
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                // connection close failed.
//                System.err.println(e);
//            }
//        }
//    }
//
//    private static Connection getConnection() {
//
//        try {
//            Class.forName("org.sqlite.JDBC");
//            Connection con = DriverManager.getConnection("jdbc:sqlite:src/Database/database.db");
//            return con;
//        } catch (ClassNotFoundException | SQLException e) {
//            JOptionPane.showMessageDialog(null, "Problem with connection of database");
//            return null;
//        }
//    }
//
//    private static void createTables(Connection connection) {
//        Statement statement;
//        try {
//            statement = connection.createStatement();
//            statement.setQueryTimeout(30);  // set timeout to 30 sec.
//
//            statement.executeUpdate("drop table if exists covering");
//            statement.executeUpdate("drop table if exists ngram");
//            statement.executeUpdate("drop table if exists ngram_covering");
//
//            statement.executeUpdate("create table covering (id integer, name string)");
//            statement.executeUpdate("create table ngram (id integer, name string)");
//            statement.executeUpdate("create table ngram_covering (nid integer, cid integer)");
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DBManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//    
//    private static void testTables(Connection connection) {
//        Statement statement;
//        try {
//            statement = connection.createStatement();
//            statement.setQueryTimeout(30);  // set timeout to 30 sec.
//
//            statement.executeUpdate("drop table if exists sentence");
//            statement.executeUpdate("drop table if exists ngram");
//            statement.executeUpdate("drop table if exists ngram_sentence");
//
//            statement.executeUpdate("create table sentence (id integer, name string)");
//            statement.executeUpdate("create table ngram (id integer, name string)");
//            statement.executeUpdate("create table ngram_sentence (nid integer, sid integer)");
//            
//            statement.executeUpdate("insert into ngram values(1, 'give')");
//            statement.executeUpdate("insert into ngram values(2, 'give')");
//            
//            int i = selectMaxId("ngram", connection);
//            
//            ResultSet rs = statement.executeQuery("select distinct id,name from ngram");
//            while (rs.next()) {
//                System.out.println("name = " + rs.getString("name") + "id = " + rs.getInt("id"));
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DBManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//    
//    public static List<CCGLexEntry> getEntries(int cid) throws ClassNotFoundException{
//        Class.forName("org.sqlite.JDBC");
//        Connection connection = null;
//        List<CCGLexEntry> entries = null;
//        try {
//            connection = getConnection();
//            entries = new ArrayList<>();
//            Statement statement = connection.createStatement();
//            
//            List<Integer> ngramIds = new ArrayList<>();
//            ResultSet rs = statement.executeQuery("select nid from ngram_covering where cid="+cid+"");
//            
//            while (rs.next()) {
//                ngramIds.add(rs.getInt("nid"));
//            }
//            
//            for(int id : ngramIds){
//                statement = connection.createStatement();
//                rs = statement.executeQuery("select name from ngram where id="+id+"");
//                CCGLexEntry e = new CCGLexEntry(rs.getString("name"), null, null);
//                entries.add(e);
//            }
//            
//            
//            int a=1;
//        } catch (Exception e) {
//            System.err.println("Problem with adding covering to db !!!");
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                System.err.println(e);
//            }
//        }
//        
//        return entries;
//    }
//
//    private static void updateTables(Connection connection, List<CCGLexEntry> entries) {
//        Statement statement;
//        try {
//            statement = connection.createStatement();
//            statement.setQueryTimeout(30);  // set timeout to 30 sec.
//            String sentence = "";
//            for (CCGLexEntry e : entries) {
//                sentence += e.getLemma() + "=";
//            }
//
//            int maxId = selectMaxId("covering", connection);
//            statement.executeUpdate("insert into covering values(" + maxId + ", '" + sentence + "')");
//
//            for (CCGLexEntry e : entries) {
//                int maxNGramId = selectMaxId("ngram", connection);
//                statement = connection.createStatement();
//                statement.executeUpdate("insert into ngram values(" + maxNGramId + ", '" + e.getLemma() + "')");
//                statement = connection.createStatement();
//                statement.executeUpdate("insert into ngram_covering values(" + maxNGramId + ", " + maxId + ")");
//            }
////            ResultSet rs = statement.executeQuery("select distinct id,name from sentence");
////            while (rs.next()) {
////                // read the result set
////                System.out.println("name = " + rs.getString("name") + "id = " + rs.getInt("id"));
////            }
////            
////            rs = statement.executeQuery("select distinct id,name from ngram");
////            while (rs.next()) {
////                // read the result set
////                System.out.println("name = " + rs.getString("name"));
////                System.out.println("id = " + rs.getInt("id"));
////            }
////            
////            rs = statement.executeQuery("select distinct sid,nid from ngram_sentence");
////            while (rs.next()) {
////                // read the result set
////                System.out.println("nid = " + rs.getInt("nid") + "sid = " + rs.getInt("sid"));
////                System.out.println();
////            }
//
//        } catch (SQLException ex) {
//            System.err.println("Problem with adding entries to the table !!!");
//            Logger.getLogger(DBManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//
//    private static int selectMaxId(String tableName, Connection connection) {
//        Statement statement;
//        int maxId = 0;
//        try {
//            statement = connection.createStatement();
//            statement.setQueryTimeout(30);  // set timeout to 30 sec.
//
//            ResultSet rs = statement.executeQuery("select max(id) as id from " + tableName);
//            while (rs.next()) {
//                maxId = rs.getInt("id");
//                return maxId + 1;
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DBManagement.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return maxId+1;
//    }
//
//    public static void addCoveringsToDB(List<Covering> coverings) throws ClassNotFoundException {
//        Class.forName("org.sqlite.JDBC");
//        Connection connection = null;
//        try {
//            connection = getConnection();
//            createTables(connection);
//            for (Covering c : coverings) {
//                updateTables(connection, c.getTokens());
//            }
//        } catch (Exception e) {
//            System.err.println("Problem with adding covering to db !!!");
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                System.err.println(e);
//            }
//        }
//
//    }
}

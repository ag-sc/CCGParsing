/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import static Database.DBManagement.excutePost;
import Decoder.Decoder;
import Decoder.Bucket;
import Derivation.Covering;
import Derivation.Interval;
import Factory.Factory;
import Grammar.CCGLexEntry;
import com.google.common.base.CharMatcher;
import com.mysql.jdbc.Connection;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import static java.sql.Types.BLOB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sherzod
 */
public class Test {

    static final String WRITE_OBJECT_SQL = "INSERT INTO lexicon(lemma, logical_form, semantics, syntax) VALUES (?, ?, ?, ?)";

    static final String READ_OBJECT_SQL = "SELECT logical_form FROM lexicon WHERE id = ?";

    public static Connection getConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/semantic_parsing";
        String username = "root";
        String password = "";
        Class.forName(driver);
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static long writeJavaObject(Connection conn, Object object) throws Exception {
        String className = object.getClass().getName();
        PreparedStatement pstmt = conn.prepareStatement(WRITE_OBJECT_SQL, Statement.RETURN_GENERATED_KEYS);

        // set input parameters
        pstmt.setString(1, "Lemma");
        pstmt.setObject(2, serialize(object));
        pstmt.setString(3, "Semantics");
        pstmt.setString(4, "Syntax");
        pstmt.executeUpdate();

        // get the generated key for the id
        ResultSet rs = pstmt.getGeneratedKeys();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt(1);
        }

        rs.close();
        pstmt.close();
        System.out.println("writeJavaObject: done serializing: " + className);
        return id;
    }

    public static Object readJavaObject(Connection conn, long id) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT_SQL);
        pstmt.setLong(1, id);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        Object object = rs.getObject(1);
        String className = object.getClass().getName();

        rs.close();
        pstmt.close();
        System.out.println("readJavaObject: done de-serializing: " + className);
        return object;
    }

    public static void main(String args[]) throws Exception {
        
        

//        Factory f = new Factory();
//        List<String> listE = f.readFile(new File("/home/sherzod/entities-qald5.txt"));
//        List<String> listA = f.readFile(new File("/home/sherzod/SemanticParsing/src/Reader/Files/entityList_QALD.model"));
//
//        System.out.println(listA.size());
//        for (String e : listE) {
//            try {
//
//                if (!e.equals("")) {
//                    String[] b = e.split("\t");
//
//                    String z = b[0].toLowerCase() + "\t" + "NP" + "\t" + b[1].replace("res:", "http://dbpedia.org/resource/");
//
//                    if (!listA.contains(z)) {
//                        listA.add(z);
//                    }
//                }
//
//            } catch (Exception ex) {
//                break;
//            }
//
//        }
//
//        System.out.println(listA.size());
//        
//        f.writeListToFile("/home/sherzod/SemanticParsing/src/Reader/Files/entityList_QALD.model", listA);
//
//        Connection conn = null;
//        try {
//            conn = getConnection();
//            System.out.println("conn=" + conn);
//            conn.setAutoCommit(false);
//            List<Object> list = new ArrayList<Object>();
//            list.add("This is a short string.");
//            list.add(new Integer(1234));
//            list.add(new Date());
//
//            String s = "Sherzod";
//
//            long objectID = writeJavaObject(conn, list);
//            conn.commit();
//            System.out.println("Serialized objectID => " + objectID);
//
//            //List listFromDatabase = (List) readJavaObject(conn, objectID);
//            byte[] d = (byte[]) readJavaObject(conn, objectID);
//
//            List message = (List) deserialize(d);
//
//            System.out.println("[After De-Serialization] String=" + message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            conn.close();
//        }
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

    public static Connection getMySqlConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/semantic_parsing";
        String username = "root";
        String password = "";

        Class.forName(driver);
        Connection conn = (Connection) DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static double Random() {
        double START = 0;
        double END = 0.5;
        Random random = new Random();
        double token = RandomNumber(START, END, random);
        return token;
    }

    public static double RandomNumber(double aStart, double aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        // get the range, casting to long to avoid overflow problems
        double range = aEnd - aStart;
        // compute a fraction of the range, 0 <= frac < range
        double fraction = (range * aRandom.nextDouble());
        double randomNumber = (fraction + aStart);
        return randomNumber;
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

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author sherzod
 */
public class QueryDataProcessor {

    private Configuration conf;
    private HTable tableSubject; //S_PO
    private HTable tablePredicate; // P_SO
    private HTable tableObject; //O_SP
    private HTable tableSubjectPredicate; //SP_O
    private HTable tablePredicateObject; // PO_S
    private HTable tableSubjectObject; //SO_P
    private HTable tableLexicon;

    public QueryDataProcessor(boolean run) {

        if (run) {
            try {
                conf = HBaseConfiguration.create();
                HBaseHelper helper = HBaseHelper.getHelper(conf);
                if (!helper.existsTable("tableSubject")) {
                    helper.createTable("tableSubject", "VALUE");
                }
                if (!helper.existsTable("tablePredicate")) {
                    helper.createTable("tablePredicate", "VALUE");
                }
                if (!helper.existsTable("tableObject")) {
                    helper.createTable("tableObject", "VALUE");
                }
                if (!helper.existsTable("tableSubjectPredicate")) {
                    helper.createTable("tableSubjectPredicate", "VALUE");
                }
                if (!helper.existsTable("tablePredicateObject")) {
                    helper.createTable("tablePredicateObject", "VALUE");
                }
                if (!helper.existsTable("tableSubjectObject")) {
                    helper.createTable("tableSubjectObject", "VALUE");
                }
                if (!helper.existsTable("tableLexicon")) {
                    helper.createTable("tableLexicon", "VALUE");
                }
                tableSubject = new HTable(conf, "tableSubject");
                tablePredicate = new HTable(conf, "tablePredicate");
                tableObject = new HTable(conf, "tableObject");

                tableSubjectPredicate = new HTable(conf, "tableSubjectPredicate");
                tablePredicateObject = new HTable(conf, "tablePredicateObject");
                tableSubjectObject = new HTable(conf, "tableSubjectObject");

                tableLexicon = new HTable(conf, "tableLexicon");

            } catch (IOException ex) {
                Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /*
     * @param subject as rowId
     * @return List of Elements with predicate and object values
     */
    public List<Element> getPredicateAndObject(String subject) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(subject));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tableSubject.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(Bytes.toString(CellUtil.cloneRow(kv)), Bytes.toString(CellUtil.cloneQualifier(kv)), Bytes.toString(CellUtil.cloneValue(kv)));
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;

    }

    /*
     * @param predicate as rowId
     * @return List of Elements with subject and object values
     */
    public List<Element> getSubjectAndObject(String predicate) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(predicate));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tablePredicate.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(Bytes.toString(CellUtil.cloneQualifier(kv)), Bytes.toString(CellUtil.cloneRow(kv)), Bytes.toString(CellUtil.cloneValue(kv)));
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;

    }

    /*
     * @param object as rowId
     * @return List of Elements with subject, predicate values
     */
    public List<Element> getSubjectAndPredicate(String object) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(object));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tableObject.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(Bytes.toString(CellUtil.cloneQualifier(kv)), Bytes.toString(CellUtil.cloneValue(kv)), Bytes.toString(CellUtil.cloneRow(kv)));
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;
    }

    /*
     * @param predicate and object as rowId
     * @return List of Elements with subject values
     */
    public List<Element> getSubject(String predicate, String object) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(predicate + "###" + object));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tablePredicateObject.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(Bytes.toString(CellUtil.cloneQualifier(kv)), predicate, object);
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;
    }
    
    /*
     * @param predicate and object as rowId
     * @return true if subject exists
     */
    public boolean checkSubject(String predicate, String object) {
        boolean exists = false;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(predicate + "###" + object));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tablePredicateObject.get(get);

            if(result.rawCells().length>0){
                exists = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
    }

    /*
     * @param subject and object as rowId
     * @return List of Elements with predicate values
     */
    public List<Element> getPredicate(String subject, String object) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(subject + "###" + object));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tableSubjectObject.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(subject, Bytes.toString(CellUtil.cloneQualifier(kv)), object);
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;
    }

    /*
     * @param subject and predicate as rowId
     * @return List of Elements with object values
     */
    public List<Element> getObject(String subject, String predicate) {
        List<Element> elements = null;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(subject + "###" + predicate));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tableSubjectPredicate.get(get);
            elements = new ArrayList<Element>();

            for (Cell kv : result.rawCells()) {
                Element e1 = new Element(subject, predicate, Bytes.toString(CellUtil.cloneQualifier(kv)));
                elements.add(e1);
            }
            return elements;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return elements;
    }

    /*
     * @param predicate as predicate
     * @return count of Elements with subject and object values
     */
    public int getSubjectAndObjectCount(String predicate) {
        int count = 0;
        try {
            conf = HBaseConfiguration.create();
            Get get = new Get(Bytes.toBytes(predicate));
            get.addFamily(Bytes.toBytes("VALUE"));
            Result result = tablePredicate.get(get);
            for (KeyValue kv : result.raw()) {
                count++;
            }
            return count;
        } catch (IOException ex) {
            Logger.getLogger(QueryDataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;

    }
}

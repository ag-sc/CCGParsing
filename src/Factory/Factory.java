package Factory;

import Feature.CCGFeature;
import Feature.Feature;
import Grammar.AtomicCategory;
import Grammar.CCGCategory;
import Grammar.CCGGrammar;
import Grammar.CCGLexEntry;
import Grammar.CombinedCategory;
import LambdaCalculus.Expression;
import LambdaCalculus.Parser.ExpressionParser;
import Reader.CCG.CCGParser;
import Reader.CCG.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Lexicon;
import de.citec.sc.matoll.core.Sense;
import de.citec.sc.matoll.io.LexiconLoader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Factory {

    private static int variableCount = 0;
    

    public Expression constructLambdaCalculusExpression(String semantics){
        
        try {

                StringReader sr = new StringReader(semantics);

                java.io.Reader r = new java.io.BufferedReader(sr);
                
                ExpressionParser parser = new ExpressionParser(r);

                Expression expr = (Expression) parser.parse();
                
                return expr;
                

            } catch (NullPointerException e) {
                //e.printStackTrace();
                System.err.println("Problem with parsing "+semantics);
            } catch (LambdaCalculus.Parser.ParseException e) {
                System.err.println("Problem with parsing "+semantics);
                //e.printStackTrace();
            }
        return null;
    }
    

    private List<String> processConjunctionArguments(String variables) {
        List<String> list = new ArrayList<>();

        variables = variables.replace("(", "");
        variables = variables.replace(")", "");

        String[] v = variables.split(",");

        for (int i = 0; i < v.length; i++) {
            list.add(v[i]);
        }

        return list;
    }
    public CCGCategory construcCCGCategory(String expression) {
        if (expression.equals("S") || expression.equals("N") || expression.equals("NP")) {
            AtomicCategory a = new AtomicCategory(expression);
            return a;
        } else {
            try {
                java.io.StringReader sr = new java.io.StringReader(expression);
                java.io.Reader r = new java.io.BufferedReader(sr);
                CCGParser parser = new CCGParser(r);
                CombinedCategory c = parser.parseCCG();
                return c;
            } catch (ParseException | NullPointerException | ClassCastException e) {
                //System.err.println("Problem parsing: " + expression);
                return null;
            }
        }
    }

    // reads grammar from file and return Grammar Object
    public CCGGrammar readGrammar(File file) {
        CCGGrammar grammar = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            List<CCGLexEntry> lexEntries = new ArrayList<CCGLexEntry>();

            while ((strLine = br.readLine()) != null) {
                try {
                    String[] entries = strLine.split("\t");

                    Expression l = constructLambdaCalculusExpression(entries[2]);

                    CCGLexEntry i = new CCGLexEntry(entries[0], construcCCGCategory(entries[1]), l);
                    lexEntries.add(i);
                } catch (Exception ee) {
                    System.out.println(strLine);
                    break;
                }

            }
            in.close();
            grammar = new CCGGrammar();
            grammar.addLexicalEntries(lexEntries);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return grammar;
    }

    // reads string from file and return
    public ArrayList<String> readFile(File file) {
        ArrayList<String> content = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (content == null) {
                    content = new ArrayList<>();
                }
                content.add(strLine);

            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }

    // reads string from file and return
    public HashMap<String, List<String>> readFileAsHashMap(File file) {
        HashMap<String, List<String>> content = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (content == null) {
                    content = new HashMap<>();
                }
                String[] entries = strLine.split("\t");

                if (entries.length > 2) {
                    if (content.containsKey(entries[0])) {
                        List<String> oldEntries = content.get(entries[0]);
                        oldEntries.add(entries[1] + "\t" + entries[2]);

                        content.put(entries[0], oldEntries);
                    } else {
                        List<String> newEntries = new ArrayList<>();
                        newEntries.add(entries[1] + "\t" + entries[2]);

                        content.put(entries[0], newEntries);
                    }
                }

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }

    // reads string from file and return
    public ArrayList<String> readMatollFile(String filePath, int minFreq) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        try {

            LexiconLoader loader = new LexiconLoader();

            Lexicon lexicon = loader.loadFromFile(filePath);
            System.out.println("Loading MATOLL Lexicon ... ");
            System.out.println(lexicon.size());
            //PrintWriter printWriter = new PrintWriter("qald5.txt");
            for (LexicalEntry entry : lexicon.getEntries()) {

                String line = entry.getReference().toString() + "\t";
                line += entry.getCanonicalForm().replace("@en", "") + "\t";
                line += entry.getPOS();
                try {
                    line += "\t" + entry.getProvenance().getFrequency();

                } catch (Exception e) {
                    //e.printStackTrace();
                }

                if (!entry.getPOS().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                    if (Integer.parseInt(entry.getProvenance().getFrequency().toString()) > minFreq) {
                        content.add(line);
                    }
                }
                if (entry.getPOS().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                    if (Integer.parseInt(entry.getProvenance().getFrequency().toString()) > 1) {
                        content.add(line);
                    }
                }

                //printWriter.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //printWriter.close();
        return content;
    }




    public void writeListToFile(String fileName, List<String> content) {
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("");

            for (String s : content) {
                bw.append(s + "\n");
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

//    //parses Geobase Logical expression and returns logical form
//    public PredicateLogicalForm constructPLExpression(String string) {
//
//        java.io.StringReader sr = new java.io.StringReader(string);
//        java.io.Reader r = new java.io.BufferedReader(sr);
//
//        LogicalFormParser parser = new LogicalFormParser(r);
//        try {
//            PredicateLogicalForm lf = parser.parse();
//            return lf;
//        } catch (Reader.LogicalForm.ParseException ex) {
//            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//
//        } catch (NullPointerException ex) {
//            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//
//        }
//        return null;
//    }

}

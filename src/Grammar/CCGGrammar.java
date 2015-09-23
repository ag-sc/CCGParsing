package Grammar;

import Feature.CCGFeature;
import Stemmer.Stemmer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CCGGrammar implements Grammar {

    HashMap<String, Set<CCGLexEntry>> entries;

    HashMap<Integer, CCGLexEntry> int2lex;

    public CCGGrammar() {
        int2lex = new HashMap<Integer, CCGLexEntry>();
        entries = new HashMap<String, Set<CCGLexEntry>>();
    }

    public void addLexicalEntries(List<CCGLexEntry> entries) {
        for (CCGLexEntry entry : entries) {
            try {
                addLexicalEntry(entry);
            } catch (Exception e) {
                System.out.println("Problem with adding entry to grammar: " + entry);
            }
        }

    }

    public void addLexicalEntry(CCGLexEntry entry) {

        Set<CCGLexEntry> temp = getEntriesforLexeme(entry.getLemma());
        if (temp == null) {
            temp = new HashSet<CCGLexEntry>();
            temp.add(entry);
            int2lex.put(int2lex.size(), entry);
        } else {
            boolean isThere = false;
            for (CCGLexEntry c : temp) {
                if (entry.getLemma().equals(c.getLemma())) {
                    if (entry.getSyntax().toString().equals(c.getSyntax().toString())) {
                        if (entry.getLambda() != null && c.getLambda() != null) {
                            if (entry.getLambda() != null) {
                                if (c.getLambda().toString().equals(entry.getLambda().toString())) {
                                    isThere = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!isThere) {
                temp.add(entry);
                int2lex.put(int2lex.size(), entry);
            }

        }

        entries.put(entry.Lemma, temp);

    }

    public void addLexicalEntry(CCGLexEntry entry, int index) {
        int2lex.put(int2lex.size(), entry);

        Set<CCGLexEntry> temp = getEntriesforLexeme(entry.getLemma());
        if (temp == null) {
            temp = new HashSet<CCGLexEntry>();
            temp.add(entry);
        } else {
            temp.add(entry);
        }

        entries.put(entry.Lemma, temp);

    }

    public Set<CCGLexEntry> getEntriesforLexeme(String lexeme) {
        if (entries.containsKey(lexeme)) {
            return entries.get(lexeme);
        } else {
            Stemmer stemmer = new Stemmer();
            String stem = stemmer.process(lexeme);

            if (entries.containsKey(stem)) {

                Set<CCGLexEntry> stemmed = entries.get(stem);
                for (CCGLexEntry e : stemmed) {
                    e.setLemma(lexeme);
                }
                return stemmed;
            }
        }
        return null;
    }

    public Set<CCGLexEntry> getEntriesforLexeme(String lexeme, int index) {
        Set<CCGLexEntry> all = null;
        Set<CCGLexEntry> featuresMatchingIndex = null;

        if (entries.containsKey(lexeme)) {
            all = entries.get(lexeme);

            for (CCGLexEntry f : all) {
                if (f.getInstanceId() == index) {
                    if (featuresMatchingIndex == null) {
                        featuresMatchingIndex = new HashSet<CCGLexEntry>();
                    }
                    featuresMatchingIndex.add(f);
                }
            }
        }
        return featuresMatchingIndex;
    }

    public List<CCGLexEntry> getAllEntries() {
        Set set = int2lex.entrySet();
        Iterator i = set.iterator();
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            entries.add((CCGLexEntry) me.getValue());
        }
        return entries;
    }

    public String toString() {
        String string = "";

        CCGLexEntry lex;

        for (Integer i : int2lex.keySet()) {
            lex = int2lex.get(i);
            string += lex.toString() + "\n";
        }

        return string;
    }

    @Override
    public Grammar merge(Grammar grammar) {
        CCGGrammar newGrammar = (CCGGrammar) grammar;

        CCGGrammar mergedGrammar = new CCGGrammar();

        mergedGrammar.addLexicalEntries(this.getAllEntries());

        mergedGrammar.addLexicalEntries(newGrammar.getAllEntries());

        return newGrammar;
    }

}

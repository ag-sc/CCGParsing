/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reader.CCG;

import Grammar.CombinedCategory;
import java.io.FileReader;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args){
        try {
            //((S\NP)/NP) in text.txt
            java.io.StringReader sr = new java.io.StringReader("(S/N)");
            java.io.Reader r = new java.io.BufferedReader(sr);
            CCGParser parser = new CCGParser(r);
            
            CombinedCategory category = (CombinedCategory) parser.parseCCG();
            int a=1;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        catch(ParseException e){
            e.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Derivation;

import Feature.CCGFeature;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author sherzod
 */
public class Test {

    public static void main(String[] args) {
        
        
        PriorityQueue<Covering> a = new PriorityQueue<Covering>(1);
        
        PriorityQueue<CCGFeature> a1 = new PriorityQueue<CCGFeature>(1);
        
//        Covering c1 = new Covering();
//        c1.setScore(0.003);
//        
//        Covering c2 = new Covering();
//        c2.setScore(0.002);
//        
//        Covering c3 = new Covering();
//        c3.setScore(0.005);
//        
//        a.add(c1);
//        a.add(c2);
//        a.add(c3);
        
        CCGFeature f1 = new CCGFeature();
        f1.setScore(1.0);
        
        CCGFeature f2 = new CCGFeature();
        f2.setScore(1.2);
        
        a1.add(f1);
        a1.add(f2);
        
        while (!a1.isEmpty()) {
            System.out.println(a1.poll().getScore());
            
        }
        
//        ArrayList<String> list = readFile("src/Reader/Files/np-fixedlex.geo");
//
//        for (String s : list) {
//            String lemma = s.substring(0, s.indexOf(" :- "));
//            s = s.replace(lemma + " :- ", "");
//            String syntax = s.substring(0, s.indexOf(" : "));
//            s = s.replace(syntax + " : ", "");
//            
//            System.out.println(lemma+"\t"+syntax+"\t"+s);
//        }

        
    }

    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> content = new ArrayList<String>();
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(fileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                content.add(strLine);
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return content;
    }
}

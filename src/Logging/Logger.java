/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author sherzod
 */
public class Logger {

    private static String filePATH = "src/Reader/Files/logs";

    public static void setFilePATH(String filePATH) {
        Logger.filePATH = filePATH;
    }

    public static void logText(String text) {

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePATH, true)));
            out.println(text);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public static void removeLogs() {
        File[] files = new File("src/Reader/Files/logs").listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }

        File f = new File("src/Reader/Files/logs.txt");
        f.delete();
    }

}

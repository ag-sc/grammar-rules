/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evalution;

import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import parser.Grammar;
import parser.GrammarFactory;
import utils.csv.CsvUtils;

/**
 *
 * @author elahi
 */
public class ChatGPT {

    public static void main(String[] args) {
        String inputDir = "result/es/";
        args = new String[]{"", inputDir, "ChatGPT"};
        String qaldDataType = "QALD9";
        String dataSetType = "test";
        String inductive = "chatGpt";
        //inductive = "inductive";
        File[] files = new File(inputDir).listFiles();
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            //throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
            exit(1);
        }
        //"In which countries do people speak Japanese?"
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald", "sparqlQueGG"};

        Evalution evalution = new Evalution();
        try {
            evalution.evalute(inputDir, qaldDataType, dataSetType, inductive);
        } catch (IOException ex) {
            Logger.getLogger(ChatGPT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

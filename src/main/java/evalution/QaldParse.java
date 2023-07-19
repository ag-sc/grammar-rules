/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evalution;

import java.io.File;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import parser.Grammar;
import parser.GrammarFactory;
import utils.StringModifier;
import utils.csv.CsvUtils;

/**
 *
 * @author elahi
 */
public class QaldParse {

    private static String grammarFileName = "grammarFiles/grammar_FULL_DATASET_EN.json";
    private static String inputDir = "result/en/";
    private static String qaldFileName = "";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    

    private static String[] runParser(Grammar grammar, String id, String status, String sentence, String sparqlGold) {
        try {
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            sparqlGold = StringModifier.deleteQuote(sparqlGold).replace("\n", "");
            String sparql = grammar.parser(sentence);
            String line = null;
            if (sparql != null) {
                //line = id + "," + "WORKS" + "," + sentence + "," + sparql + "," + sparqlGold;
                return new String[]{id, status, sentence, sparql, sparqlGold};
            } else {
                return new String[]{id, status, sentence, "N", sparqlGold};
            }

            //str += line + "\n";
        } catch (Exception ex) {
            Logger.getLogger(QaldParse.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return new String[]{};
    }
    
    public static void main(String[] args) {
        String inputDir = "result/en/";
        args = new String[]{"en", inputDir, "QALD9"};
        String language = args[0];
        Grammar grammar = null;
        try {
            grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(QaldParse.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        Boolean parseFlag = true, evaluateFlag = false;
        String qaldDataType = "QALD9";
        String dataSetType = "train";
        String inductive = "incremental";
        File[] files = new File(inputDir).listFiles();
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            //throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
            exit(1);
        }
        //"In which countries do people speak Japanese?"
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald", "sparqlQueGG"};
        if (parseFlag) {
            System.out.println("Grammar Parser!!!");
            for (File file : files) {
                if (file.getName().contains("input-") && file.getName().contains(qaldDataType)
                        && file.getName().contains(dataSetType)
                        && file.getName().contains(inductive)) {
                    List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
                    File outputFile = new File(inputDir + file.getName().replace("input-", "output-"));
                    List<String[]> outputs = new ArrayList<String[]>();
                    outputs.add(header);
                    Integer limit = 51;
                    Integer index = 0, countWork = 1;
                    for (String[] row : rows) {
                        /*if(!row[0].equals("44")){
                            continue;  
                         }*/
                        System.out.println(row[0] + " " + row[1] + " " + row[2] + " " + row[3]);
                        String[] result = runParser(grammar, row[0], row[1], row[2], row[3]);
                        outputs.add(result);
                        System.out.println("result::" + result[0] + " " + result[1] + " " + result[2] + " " + result[3]);
                        /*if (row[1].contains("WORK")) {
                            countWork = countWork + 1;
                        }*/

                        index = index + 1;
                        if (limit == -1)
                                ; else if (index >= limit) {
                            break;
                        }

                        //}
                        if (limit == -1)
                                ; else if (index >= limit) {
                            break;
                        }

                    }
                    try {
                        System.out.println("countWork::" + countWork + " row:Size::" + rows.size());
                        CsvUtils.writeDataAtOnce(outputFile, outputs);
                    } catch (Exception ex) {
                        Logger.getLogger(QaldParse.class.getName()).log(Level.SEVERE, null, ex);
                        ex.getMessage();
                    }

                }
            }
        }

    }

}

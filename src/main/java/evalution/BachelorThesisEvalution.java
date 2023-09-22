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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.math.NumberUtils;
import org.fest.util.Arrays;
import parser.Grammar;
import parser.GrammarFactory;
import utils.Dictionary;
import utils.StringModifier;
import utils.csv.CsvUtils;

/**
 *
 * @author elahi
 */
public class BachelorThesisEvalution {

    private static String grammarFileName = "grammarFiles/en/grammar_FULL_DATASET_EN.json";
    private static String inputDir = "result/de/";
    private static String qaldFileName = "";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    private static String[] runParser(Grammar grammar, String id, String status, String sentence, String sparqlGold) {
       /* try {
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
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }*/
        return new String[]{};
    }

    private static String[] runParserOffLine(String id, String status, String sentence, String sparqlGold) {
        try {
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            String sparql = StringModifier.deleteQuote(sparqlGold).replace("\n", "");
            if (sparql != null) {
                if (status.contains("WORK")) {
                    return new String[]{id, status, sentence, sparqlGold, sparql};
                } else {
                    return new String[]{id, status, sentence, sparqlGold, "N"};
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return new String[]{};
    }

    private static List<String[]> parseOffLine(List<String[]> inputRows, String[] header) {
        List<String[]> outputs = new ArrayList<String[]>();
        Integer limit = -1;
        Integer index = 0, countWork = 1;
        for (String[] row : inputRows) {
            /*if(!row[0].equals("44")){
                    continue;
                    }*/
            System.out.println(row[0] + " " + row[1] + " " + row[2] + " " + row[3]);
            //String[] result = runParser(grammar, row[0], row[1], row[2], row[3]);
            String[] result = runParserOffLine(row[0], row[1], row[2], row[3]);
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
        return outputs;

    }

    /*public static void main(String[] args) {
        String inputDir = "result/en/";
        args = new String[]{"", inputDir, "QALD9"};
        String language = args[0];
        Grammar grammar = null;
        try {
            grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(QaldParse.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        Boolean parseFlag = false, evaluateFlag = false, incrementalFlag = true;
        String qaldDataType = "QALD9";
        String dataSetType = "test";
        String inductive = "incremental";
        //inductive = "inductive";
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
                    File outputFile = new File(inputDir + file.getName().replace("input-", "output-"));
                    List<String[]> inputRows = CsvUtils.readAllDataAtOnce(file);
                    List<String[]> outputs = parseOffLine(inputRows, header);
                    try {
                        //System.out.println("countWork::" + countWork + " row:Size::" + inputRows.size());
                        CsvUtils.writeDataAtOnce(outputFile, outputs);
                    } catch (Exception ex) {
                        Logger.getLogger(QaldParse.class.getName()).log(Level.SEVERE, null, ex);
                        ex.getMessage();
                    }
                }
            }
        }
        if (evaluateFlag) {
            Evalution evalution = new Evalution();
            evalution.evalute(inputDir, qaldDataType, dataSetType, inductive);
        }
        if (incrementalFlag) {
            String[] languages = new String[]{"en"};
            qaldDataType = "QALD9";
            dataSetType = "train";
            inductive = "inductive";
            String incremental="incremental";
            
            for (String languageT : languages) {
                inputDir = "result/" + languageT + "/";
                
                File inductiveFile = new File(inputDir + "output-"+qaldDataType+"-"+dataSetType+"-"+inductive+".csv");
                File incrementalFile = new File(inputDir + "output-"+qaldDataType+"-"+dataSetType+"-"+incremental+".csv");
                List<String[]> newLexicalEntries = findNewEntries(incrementalFile,dataSetType);
                List<String[]> inputRows = CsvUtils.readAllDataAtOnce(inductiveFile);
                List<String[]> results = new ArrayList<String[]>();
                String[] resultHead = new String[]{"id", "lexicalEntry", "macro P", "macro R", "macro F", "micro-P", "micro-R", "micro-F"};
                results.add(resultHead);
                if(dataSetType.contains("test")){
                    if(languageT.contains("en"))
                       results.add(new String[]{"inductive", "en","0.988924", "0.14705883", "0.25604263", "0.24", "0.2358013", "0.23693568"});  
                    else if(languageT.contains("de"))
                       results.add(new String[]{"inductive", "de","0.9912892", "0.13388236", "0.23590383", "0.19333333", "0.18913463", "0.19026901"});  

                }
                Integer index = 1;
                for (String[] newLexicalEntry : newLexicalEntries) {
                    Integer microIndex=0,macroindex=0;
                    String newLexicalEntryID = newLexicalEntry[0];
                    String question = newLexicalEntry[2];
                    File outputFile = new File(inputDir + incrementalFile.getName().replace("output-", index+"-" + "output-"+newLexicalEntryID+"-" ));
                    String[]head=new String[]{"ID","status","sentence","sparqlQueGG","sparqlQald"};
                    List<String[]> inputNewRows =new ArrayList<String[]>();
                    inputNewRows.add(head);
                    inputNewRows = addLexicalEntryToFile(newLexicalEntry, inputRows);
                   
                    Evalution evalution = new Evalution();
                    List<String[]> outputs = evalution.evalute(inputNewRows);
                    if(dataSetType.contains("test")){
                       microIndex=151;
                       macroindex=152;  
                    }
                    else{
                         microIndex=409;
                         macroindex=410;  
                    }
                    String[] result_1 = outputs.get(microIndex);
                    String[] result_2 = outputs.get(macroindex);
                    System.out.println(newLexicalEntry.length + " now::" + index + " id::" + newLexicalEntryID + " ques::" + question);
                    System.out.println(result_1[8] + " " + result_1[9] + " " + result_1[10]);
                    System.out.println(result_2[8] + " " + result_2[9] + " " + result_2[10]);
                    results.add(new String[]{newLexicalEntryID, question, result_1[8], result_1[9], result_1[10], result_2[8], result_2[9], result_2[10]});
                    
                    CsvUtils.writeDataAtOnce(outputFile, outputs);
                   
                    index = index + 1;
                    
                    inputRows =new ArrayList<String[]>();
                    inputRows.addAll(inputNewRows);

                     
                }
                CsvUtils.writeDataAtOnce(new File(inputDir + "9000" + "-output-"+dataSetType+"-"+"incremental.csv"), results);

            }

        }

    }*/
    private static List<String[]> addLexicalEntryToFile(List<String[]> newLexicalEntriesRows, List<String[]> inputRows) {
        List<String[]> outputs = new ArrayList<String[]>();
        Map<String, String[]> newLexicalEntriesMap = new TreeMap<String, String[]>();
        for (String[] newRow : newLexicalEntriesRows) {
            newLexicalEntriesMap.put(newRow[0], newRow);
        }

        for (String[] row : inputRows) {
            String id = row[0];
            if (newLexicalEntriesMap.containsKey(id)) {
                String[] rowNew = newLexicalEntriesMap.get(id);
                //System.out.println(row[0]+" id:" + rowNew[0]);
                //System.out.println(row[1]+" status:" + rowNew[1]);
                //System.out.println(row[3]+" sparql_1:" + rowNew[3]);
                //System.out.println(row[4]+" sparql_2:" + rowNew[4]);
                outputs.add(rowNew);
            } else {
                outputs.add(row);
            }
        }
        return outputs;
    }

    private static Map<String, List<String[]>> findNewEntries(File incrementalFile, String dataType) {
        Map<String, List<String[]>> outputs = new TreeMap<String, List<String[]>>();
        List<String[]> incrementalRows = CsvUtils.readAllDataAtOnce(incrementalFile);
        for (String[] row : incrementalRows) {
            String status = row[1];
            status = status.toLowerCase();
            Boolean flag = false;
            if (dataType.contains("test") && status.equals("worknew")) {
                flag = true;
            } else if (dataType.contains("train") && NumberUtils.isDigits(status)) {
                flag = true;
            }
            List<String[]> existRows = new ArrayList<String[]>();
            if (flag) {
                if (outputs.containsKey(status)) {
                    existRows = outputs.get(status);
                }
                existRows.add(row);
                outputs.put(status, existRows);

            }

        }
        return outputs;
    }

    public static void onlineEvalution(String language, String classFileName) {

        Boolean parseFlag = false, evaluateFlag = true, incrementalFlag = false;
        String qaldDataType = "QALD7";
        String dataSetType = "train";
        String inductive = "inductive";
        File[] files = new File(inputDir).listFiles();
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald", "sparqlQueGG"};
        /*if (parseFlag) {
            Grammar grammar = null;
            try {
                grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language, classFileName).getGrammar();
            } catch (Exception ex) {
                Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
            System.out.println("Grammar Parser!!!");
            for (File file : files) {
                if (file.getName().contains("input-") && file.getName().contains(qaldDataType)
                        && file.getName().contains(dataSetType)
                        && file.getName().contains(inductive)) {
                    File outputFile = new File(inputDir + file.getName().replace("input-", "output-"));
                    List<String[]> inputRows = CsvUtils.readAllDataAtOnce(file);
                    List<String[]> outputs = parseOffLine(inputRows, header);
                    try {
                        //System.out.println("countWork::" + countWork + " row:Size::" + inputRows.size());
                        CsvUtils.writeDataAtOnce(outputFile, outputs);
                    } catch (Exception ex) {
                        Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
                        ex.getMessage();
                    }
                }
            }
        }*/
        if (evaluateFlag) {
            Evalution evalution = new Evalution();
            try {
                evalution.evalute(inputDir, qaldDataType, dataSetType, inductive);
            } catch (IOException ex) {
                Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (incrementalFlag) {
        /*    String[] languages = new String[]{"en"};
            qaldDataType = "QALD9";
            dataSetType = "train";
            inductive = "inductive";
            String incremental = "incremental";

            for (String languageT : languages) {
                inputDir = "result/" + languageT + "/";

                File inductiveFile = new File(inputDir + "output-" + qaldDataType + "-" + dataSetType + "-" + inductive + ".csv");
                File incrementalFile = new File(inputDir + "output-" + qaldDataType + "-" + dataSetType + "-" + incremental + ".csv");
                Map<String, List<String[]>> newLexicalEntries = findNewEntries(incrementalFile, dataSetType);
                List<String[]> inputRows = CsvUtils.readAllDataAtOnce(inductiveFile);
                List<String[]> results = new ArrayList<String[]>();
                String[] resultHead = new String[]{"id", "lexicalEntry", "macro P", "macro R", "macro F", "micro-P", "micro-R", "micro-F"};
                results.add(resultHead);
                Integer index = 1;
                for (String newLexicalEntryID : newLexicalEntries.keySet()) {
                    Integer microIndex = 0, macroindex = 0;
                    List<String[]> newLexicalRows = newLexicalEntries.get(newLexicalEntryID);
                    File outputFile = new File(inputDir + incrementalFile.getName().replace("output-", index + "-" + "output-" + newLexicalEntryID + "-"));
                    String[] head = new String[]{"ID", "status", "sentence", "sparqlQueGG", "sparqlQald"};
                    List<String[]> inputNewRows = new ArrayList<String[]>();
                    inputNewRows.add(head);
                    inputNewRows = addLexicalEntryToFile(newLexicalRows, inputRows);
                    Evalution evalution = new Evalution();
                    List<String[]> outputs = evalution.evalute(inputNewRows);
                    String[] result_1 = outputs.get(microIndex);
                    String[] result_2 = outputs.get(macroindex);
                    System.out.println(newLexicalEntries.size() + " now::" + index + " id::" + newLexicalEntryID);
                    System.out.println(result_1[8] + " " + result_1[9] + " " + result_1[10]);
                    System.out.println(result_2[8] + " " + result_2[9] + " " + result_2[10]);
                    results.add(new String[]{newLexicalEntryID, newLexicalEntryID, result_1[8], result_1[9], result_1[10], result_2[8], result_2[9], result_2[10]});

                    CsvUtils.writeDataAtOnce(outputFile, outputs);

                    index = index + 1;

                    inputRows = new ArrayList<String[]>();
                    inputRows.addAll(inputNewRows);

                }
                CsvUtils.writeDataAtOnce(new File(inputDir + "9000" + "-output-" + dataSetType + "-" + "incremental.csv"), results);

            }
        */
        }

    }

    public static void main(String[] args) {
        String language = "de";
        Grammar grammar = null;
        String inputDir = "result/de/";
        String classFileName = "src/main/resources/LexicalEntryForClass.csv";
        onlineEvalution(language, classFileName);
    }

}

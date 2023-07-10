
import evalution.Evalution;
import evalution.QALD;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NoArgsConstructor;
import parser.Grammar;
import parser.GrammarFactory;
import parser.GrammarRule;
import utils.CsvFile;
import utils.FileUtils;
import utils.FscoreCalculation;
import utils.SparqlQuery;
import utils.StringModifier;
import utils.TripleProcess;
import utils.csv.CsvUtils;
import static utils.csv.CsvUtils.readAllDataAtOnce;

@NoArgsConstructor
public class QueGG {

    private static String grammarFileName = "/home/elahi/A-Grammar/multilingual-grammar-generator/result/en/grammar_FULL_DATASET_EN.json";
    private static String inputDir = "result/en/";
    private static String qaldFileName = "";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    public static void main(String[] args) {
        String inputDir =  "result/en/";
        args = new String[]{"en", inputDir, "QALD9"};
        String language = args[0];
        Grammar grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        Boolean parseFlag = true, evaluateFlag = false;
        File[] files = new File(inputDir).listFiles();
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            //throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
            exit(1);
        }
        
        if (parseFlag) {
            System.out.println("Grammar Parser!!!");
            for (File file : files) {
                if (file.getName().contains("input-") && file.getName().contains("QALD9")) {
                    List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
                    File outputFile = new File(inputDir + file.getName().replace("input-", "output-"));
                    List<String[]> outputs = new ArrayList<String[]>();
                    Integer limit = 1; Integer index = 0;
                    for (String[] row : rows) {
                        for (String cell : row) {
                            String[] data = cell.split("\t");
                            //System.out.println(data[0] + " " + data[1] + " " + data[2]);
                            String id = data[0];
                            String sentence = data[2];
                            String sparqlGold = data[3];
                            sentence="Who created the comic Captain America?";
                            String[] result = runParser(grammar, id, sentence, sparqlGold);
                            outputs.add(result);
                            index = index + 1;
                            //break;
                            if (limit == -1)
                                ; else if (index >= limit) {
                                break;
                            }
                          

                        }
                        if (limit == -1)
                                ; else if (index >= limit) {
                            break;
                        }

                    }
                    try {
                        //System.out.println(str);
                        CsvUtils.writeDataAtOnce(outputFile, outputs);
                        //FileUtils.stringToFile(str, outputFileName);
                    } catch (Exception ex) {
                        Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
                        ex.getMessage();
                    }

                }
            }
        } else if (evaluateFlag) {
            System.out.println("evaluate with QALD!!!");
            for (File file : files) {
                if (file.getName().contains("output-") && file.getName().contains("QALD9")) {
                    
                    List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
                    File outputFile = new File(inputDir + file.getName().replace("output-", "evaluation-"));
                    List<String[]> outputs = new ArrayList<String[]>();
                    outputs.add(new String[]{"ID", "status", "sentence", "sparqlQueGG", "sparqlQald", 
                                              "TP","FP","FN","Precision","Recall","Fscore"});
                    float globalTp = 0, globalFp = 0, globalFn = 0, totalPreision = 0, totalRecall = 0, totalF_measure = 0;
                    Integer numberOfMatch=0;
                    Integer limit = -1, index = 0;
                    for (String[] row : rows) {
                        Integer rowIndex = 0;
                        String id = null, sparqlQueGG = null, sparqlQald = null, status = null, sentence = null;
                        for (String cell : row) {
                            //String[] data = cell.split("\n");
                            if (rowIndex == 0) {
                                id = cell;
                            } else if (rowIndex == 1) {
                                status = cell;
                            } else if (rowIndex == 2) {
                                sentence = cell;
                            } else if (rowIndex == 3) {
                                sparqlQueGG = cell;
                            } else if (rowIndex == 4) {
                                sparqlQald = cell;
                            }
                            rowIndex = rowIndex + 1;
                        }
                        if (sparqlQueGG != null) {
                            if (!sparqlQueGG.contains("-")) {
                                Map<String, String> resultQueGG = new SparqlQuery(sparqlQueGG).getEntityMap();
                                Map<String, String> resultQald = new SparqlQuery(sparqlQald).getEntityMap();
                                System.out.println(id + " queGGSparql=" + sparqlQueGG + " qaldSparql=" + sparqlQald);
                                System.out.println(id + " resultQueGG=" + resultQueGG.size() + " resultQald=" + resultQald.size());
                                Integer numberQueGG = resultQueGG.size(), numberQald = resultQald.size();
                                FscoreCalculation cal=new FscoreCalculation(resultQueGG.keySet(),resultQald.keySet());
                                System.out.println(cal.getTp()+" "+cal.getFn()+" "+cal.getFn());
                                System.out.println(cal.getPrecision()+" "+cal.getRecall()+" "+cal.getFscore());
                                float tp=cal.getTp();
                                float fp=cal.getFp();
                                float fn=cal.getFn();
                                float precision=cal.getPrecision();
                                float recall=cal.getRecall();
                                float fscore=cal.getFscore();
                                outputs.add(new String[]{id, status, sentence, sparqlQueGG, sparqlQald, 
                                                         Float.toString(tp),Float.toString(fp),Float.toString(fn),
                                                         Float.toString(precision),Float.toString(recall),Float.toString(fscore)});
                                globalTp += tp;
                                globalFp += fp;
                                globalFn += fn;
                                totalPreision += precision;
                                totalRecall += recall;
                                totalF_measure += fscore;
                            }

                        }
                    }
                    try {
                        //System.out.println(str);
                        outputs.add(new String[]{"", "", "", "", "", 
                                                         Float.toString(globalTp),Float.toString(globalFp),Float.toString(globalFn),
                                                         Float.toString(totalPreision),Float.toString(totalRecall),Float.toString(totalF_measure)});
                        CsvUtils.writeDataAtOnce(outputFile, outputs);
                        //FileUtils.stringToFile(str, outputFileName);
                    } catch (Exception ex) {
                        Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
                        ex.getMessage();
                    }
                }
            }
        }

    }

    private static String[] runParser(Grammar grammar, String id, String sentence, String sparqlGold) {
        try {
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            sparqlGold = StringModifier.deleteQuote(sparqlGold).replace("\n", "");
            String sparql = grammar.parser(sentence);
            String line = null;
            if (sparql != null) {
                System.out.println(" sparql:" + sparql);
                //line = id + "," + "WORKS" + "," + sentence + "," + sparql + "," + sparqlGold;
                return new String[]{id, "WORKS", sentence, sparql, sparqlGold};
            } else {
                //line = id + "," + "-" + "," + sentence + "," + "-" + "," + sparqlGold;
                System.out.println(" sparql:" + sparql);
                return new String[]{id, "-", sentence, "-", sparqlGold};
            }

            //str += line + "\n";
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return new String[]{};
    }

}

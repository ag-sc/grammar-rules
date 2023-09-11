package evalution;


import java.io.File;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NoArgsConstructor;
import parser.Grammar;
import parser.GrammarFactory;
import utils.Dictionary;
import utils.StringModifier;
import utils.StringModifier;
import utils.csv.CsvFile;
import utils.csv.CsvUtils;

@NoArgsConstructor
public class BachelorThesisEvalution {

    //private static String inputDir = "grammarFiles/en/";
    private static String inputDir ="/media/elahi/Elements/BachelorThesis/results_csv/";
    private static String classFileName = "src/main/resources/LexicalEntryForClass.csv";
    //private static String grammarFileName = "grammar_FULL_DATASET_EN.json";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;
    
    public static void main(String[] args) throws Exception {
        args = new String[]{"de", "grammar_FULL_DATASET_DE.json", "grammarFiles/de/input-QALD9-train-inductive.csv"};
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        }
        String language=args[0];
        String inputFileName=args[1];
        String[] dirs = new File(inputDir).list();
        String[] folders = new String[]{"Conviction","Cosine","Leverage","Lift"};

        for (String dir : dirs) {
            for (String folder : folders) {
                if (dir.contains(folder)) {
                    String grammarFileName=inputDir+dir+File.separator+"grammar_FULL_DATASET_DE.json";
                    String outputFileName= inputFileName.replace("input", folder+"-output");
                    runParse(language,grammarFileName,inputFileName,outputFileName);
                    break;
                }
            }
        }
        /*String language = "de";
        String grammarFile = grammarDir + "grammar_FULL_DATASET_DE.json";
        String inputFileName = "grammarFiles/de/input-QALD9-train-inductive.csv";*/

    }

    public static void runParse(String language,String grammarFileName,String inputFileName,String outputFileName) throws Exception {
        Boolean parseFlag=false,evaluationFlag=true;
        //System.out.println(inputDir+"grammar_FULL_DATASET_EN_LAST_Test.json");
        Grammar grammar = loadGrammar(grammarFileName,language,classFileName);
        File outputFile = new File(outputFileName);
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald"};
        System.out.println("Grammar Parser!!!");
        File file = new File(inputFileName);
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
       
        List<String[]> outputs = new ArrayList<String[]>();
        outputs.add(header);
        Integer index=0;
        for (String[] row : rows) {
            String id=null,sentence=null,givenSparql=null;
            id=row[0];sentence=row[1];
            Integer idInteger=Integer.parseInt(id);
            if(row.length>=3){
               givenSparql=row[2]; 
            }
            /*if(idInteger!=118)
               continue;*/ 
            
            System.out.println(id+" sentence::" + sentence+" row.length::"+row.length);
            if (parseFlag) {
                String[] result = runParser(grammar, id, sentence);
                System.out.println("result::"+ id+" " +result[2] + " \n" + result[3]+" "+result[1]);
                outputs.add(result);
            }
            else{
                String[] result = runParserEvaluate(grammar, id, sentence,givenSparql);
                if(idInteger!=94)
                  System.out.println("result::"+ id+" " +result[2] + " " + result[4]+" "+result[1]);
                outputs.add(result); 
            }

            index=index+1;
            
            System.out.println();
            
            /*if(index>20)
                break;*/
            
        }
        try {
            CsvUtils.writeDataAtOnce(outputFile, outputs);
        } catch (Exception ex) {
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            ex.getMessage();
        }

    }

    private static String[] runParser(Grammar grammar, String id,  String sentence) throws Exception {
        try {
            if(id.equals("94"))
               return new String[]{id, "N", sentence, "N"};
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            String sparql = grammar.parser(sentence);
            if (sparql != null) {
                return new String[]{id, "WORK", sentence, sparql};
            } else {
                return new String[]{id, "N", sentence, "N"};
            }
        } catch (Exception ex) {
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Parsing failed for the text:"+sentence);

        }
    }
    
    private static String[] runParserEvaluate(Grammar grammar, String id, String sentence, String givenSparql) throws Exception {
        try {
            if (id.equals("94")) {
                return new String[]{id, "N", sentence, "N"};
            }
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            String sparql = grammar.parser(sentence);
            if (sparql != null) {
                return new String[]{id, "WORK", sentence, givenSparql,sparql};
            } else {
                return new String[]{id, "N", sentence, givenSparql,"N"};
            }
        } catch (Exception ex) {
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Parsing failed for the text:" + sentence);

        }
    }

    private static Grammar loadGrammar(String grammarFileName, String language,String classFileName) throws Exception {
         try {
            return new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language,classFileName).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(BachelorThesisEvalution.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Grammar loading fail!!!!");
        }
        
    }
     

}

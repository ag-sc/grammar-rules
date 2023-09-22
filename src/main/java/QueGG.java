
import com.fasterxml.jackson.databind.ObjectMapper;
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
import utils.GrammarEntries;
import utils.Result;
import utils.Results;
import utils.StringModifier;
import utils.csv.CsvFile;
import utils.csv.CsvUtils;

@NoArgsConstructor
public class QueGG {

    //private static String inputDir = "grammarFiles/en/";
    private static String inputDir ="result/en/";
    private static String classFileName = "src/main/resources/LexicalEntryForClass.csv";
    //private static String grammarFileName = "grammar_FULL_DATASET_EN.json";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    public static void main(String[] args) throws Exception {
        Boolean parseFlag=false,evaluationFlag=true;
        args = new String[]{"en", "grammarFiles/en/grammar_FULL_DATASET_EN.json","grammarFiles/en/input-QALD9-train-inductive.csv"};
        //System.out.println(inputDir+"grammar_FULL_DATASET_EN_LAST_Test.json");
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        }
        String language = args[0];
        String grammarFileName=args[1];
        String inputFileName = args[2];
        Grammar grammar = loadGrammar(grammarFileName,language,classFileName);
        
        //String[] header = new String[]{"ID", "status", "sentence", "sparqlQald"};
        System.out.println("Grammar Parser!!!");
        File file = new File(inputFileName);
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
        File outputFile = new File(inputFileName.replace("input", "output").replace(".csv", ".json"));
        //List<String[]> outputs = new ArrayList<String[]>();
        //outputs.add(header);
        Integer index=0;
        List<Result> parseResults=new ArrayList<Result>();
        for (String[] row : rows) {
            String id=null,sentence=null,givenSparql=null;
            id=row[0];sentence=row[1];
            Integer idInteger=Integer.parseInt(id);
            Result parseResult = null;
            if(row.length>=3){
               givenSparql=row[2]; 
            }
            //if(idInteger!=34)
            //   continue; 
            
            System.out.println(id+" sentence::" + sentence+" row.length::"+row.length);
            if (parseFlag) {
                 parseResult = runParser(grammar, id, sentence);
                //System.out.println("result::"+ id+" " +result[2] + " \n" + result[3]+" "+result[1]);
                //outputs.add(result);
            }
            else {
                parseResult = runParserForQald(grammar, id, sentence, givenSparql);
                if (idInteger != 94) {
                    //System.out.println("result::"+ id+" " +result[2] + " " + result[4]+" "+result[1]);
                    
                    //outputs.add(result);  
                    parseResults.add(parseResult);
                }
               

            }

            index=index+1;
            
            System.out.println();
            
            if(index>100)
                break;
            
        }
        try {
             ObjectMapper mapper = new ObjectMapper();
             Results results=new Results(parseResults);
             mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, results);
            //CsvUtils.writeDataAtOnce(outputFile, outputs);
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            ex.getMessage();
        }

    }

    private static Result runParser(Grammar grammar, String id,  String sentence) throws Exception {
        try {
            if(id.equals("94"))
               return new Result(id, "N", sentence, new ArrayList<String>());
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            List<String> sparqls = grammar.parser(sentence);
            if (sparqls.isEmpty()) {
                return new Result(id, "WORK", sentence, (ArrayList<String>) sparqls);
            } else {
                return new Result(id, "N", sentence,  new ArrayList<String>());
            }
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Parsing failed for the text:"+sentence);

        }
    }
    
    private static Result runParserForQald(Grammar grammar, String id, String sentence, String givenSparql) throws Exception {
        try {
            if (id.equals("94")) {
                return new Result(id, "N", sentence, givenSparql,new ArrayList<String>());
            }
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            List<String> sparqls = grammar.parser(sentence);
            if (!sparqls.isEmpty()) {
                return new Result(id, "WORK", sentence, givenSparql,sparqls);
            } else {
                return new Result(id, "N", sentence, givenSparql,new ArrayList<String>());
            }
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Parsing failed for the text:" + sentence);

        }
    }

    private static Grammar loadGrammar(String grammarFileName, String language,String classFileName) throws Exception {
         try {
            return new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language,classFileName).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Grammar loading fail!!!!");
        }
        
    }
     

}

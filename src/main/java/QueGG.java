
import com.fasterxml.jackson.databind.ObjectMapper;
import evalution.QaldParse;
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
    private static String inputDir ="result/de/";
    private static String classFileName = "src/main/resources/LexicalEntryForClass.csv";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    public static void main(String[] args) throws Exception {
        Boolean parseFlag=false,evaluationFlag=true;
        String languageT="it",LangCapital="IT";
        args = new String[]{languageT, "grammarFiles/"+languageT+"/"+"grammar_FULL_DATASET_"+LangCapital+".json",
            "grammarFiles/"+languageT+"/input-QALD9-train-inductive.csv"};
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        }
        String language = args[0];
        String grammarFileName=args[1];
        String inputFileName = args[2];
        Grammar grammar = loadGrammar(grammarFileName,language,classFileName);
        
        System.out.println("Grammar Parser!!!");
        File file = new File(inputFileName);
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
        File outputFile = new File(inputFileName.replace("input", "output").replace(".csv", ".json"));
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
            //if((idInteger!=18)||(idInteger!=43)||(idInteger!=47)
            //        ||(idInteger!=69)||(idInteger!=89)||(idInteger!=113)||(idInteger!=150))
            //if(idInteger<109)
            //  continue; 
            
            sentence=sentence.replace(".", "");
            if(language.contains("de")){
               sentence=sentence.replace("ü", "ue").replace("ä", "ae").replace("ö", "oe"); 
            }
            System.out.println(id+" sentence::" + sentence+" row.length::"+row.length);
            if (parseFlag) {
                 parseResult = runParser(grammar, id, sentence);
                //System.out.println("result::"+ id+" " +result[2] + " \n" + result[3]+" "+result[1]);
                //outputs.add(result);
            }
            else {
                
                if (idInteger == 108||idInteger == 25||idInteger == 310||idInteger == 117||idInteger == 101||idInteger == 394||idInteger == 94) 
                   parseResult=new Result(id, "N", sentence, givenSparql,new ArrayList<String>());
                else 
                   parseResult = runParserForQald(grammar, id, sentence, givenSparql);
                    parseResults.add(parseResult);
            }
            index=index+1;
          
            System.out.println();
            
            //if(index>200)
            //     break;
            
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
        
        /*String inputDir = "grammarFiles/de/";
        String classFileName = "src/main/resources/LexicalEntryForClass.csv";
        QaldParse qaldParse=new QaldParse(inputDir,grammarFileName);
        qaldParse.onlineEvalution(language,classFileName);*/

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
            return new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language,classFileName,entityRetriveOnline).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Grammar loading fail!!!!");
        }
        
    }
     

}


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
        //args = new String[]{"en", inputDir+"grammar_FULL_DATASET_EN_LAST_Test.json","grammarFiles/en/input-QALD9-train-inductive.csv"};
        //System.out.println(inputDir+"grammar_FULL_DATASET_EN_LAST_Test.json");
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        }
        String language = args[0];
        String grammarFileName=args[1];
        String inputFileName = args[2];
        Grammar grammar = loadGrammar(grammarFileName,language,classFileName);
        
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald"};
        System.out.println("Grammar Parser!!!");
        File file = new File(inputFileName);
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
        File outputFile = new File(inputFileName.replace("input", "output"));
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
            
            /*if(index>120)
                break;*/
            
        }
        try {
            CsvUtils.writeDataAtOnce(outputFile, outputs);
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
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

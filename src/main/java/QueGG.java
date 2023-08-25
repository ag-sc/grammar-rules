
import java.io.File;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NoArgsConstructor;
import parser.Grammar;
import parser.GrammarFactory;
import utils.StringModifier;
import utils.csv.CsvUtils;

@NoArgsConstructor
public class QueGG {

    //private static String inputDir = "grammarFiles/en/";
    //private static String inputDir = "/home/elahi/A-Grammar/multilingual-grammar-generator/result/en/";
    //private static String grammarFileName = "grammar_FULL_DATASET_EN.json";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    public static void main(String[] args) throws Exception {
        //args = new String[]{"en", inputDir+"grammar_FULL_DATASET_EN.json","grammarFiles/en/input.csv"};
       
        if (args.length < 3) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        }
        String language = args[0];
        String grammarFileName=args[1];
        String inputFileName = args[2];
        Grammar grammar = loadGrammar(grammarFileName,language);
        
        String[] header = new String[]{"ID", "status", "sentence", "sparqlQald"};
        System.out.println("Grammar Parser!!!");
        File file = new File(inputFileName);
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
        File outputFile = new File(inputFileName.replace("input", "output"));
        List<String[]> outputs = new ArrayList<String[]>();
        outputs.add(header);
        for (String[] row : rows) {
            String id=row[0];
            String sentence=row[1];
            //sentence="Where was Bach born?";
            Integer idInteger=Integer.parseInt(id);
            /*if(idInteger!=1)
               continue; */
            //System.out.println(id+" sentence::" + sentence);
            //sentence="What films does Jesse Eisenberg play in?";
            String[] result = runParser(grammar, id, sentence);
            outputs.add(result);
            System.out.println("result::"+ id+" " +result[2] + " " + result[3]+" "+result[1]);
            System.out.println();
            
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
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            String sparql = grammar.parser(sentence);
            if (sparql != null) {
                return new String[]{id, "Parsed", sentence, sparql};
            } else {
                return new String[]{id, "Not Parsed", sentence, "N"};
            }
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Parsing failed for the text:"+sentence);

        }
    }

    private static Grammar loadGrammar(String grammarFileName, String language) throws Exception {
         try {
            return new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        } catch (Exception ex) {
            Logger.getLogger(QueGG.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Grammar loading fail!!!!");
        }
        
    }

}


import evalution.Evalution;
import evalution.QALD;
import java.io.File;
import java.util.Set;
import lombok.NoArgsConstructor;
import parser.Grammar;
import parser.GrammarFactory;
import parser.GrammarRule;
import utils.TripleProcess;

@NoArgsConstructor
public class QueGG {
    private static String grammarFileName="src/main/resources/grammar_FULL_DATASET_EN.json";
    private static String qaldFileName="";
    private static Boolean entityRetriveOnline=true;
    private static Integer numberOfEntities=-1;
     private static String language="en";

    public static void main(String[] args) throws Exception {
        System.out.println("Grammar Parser!!!");
        
        Grammar grammar=new GrammarFactory(new File(grammarFileName),entityRetriveOnline,numberOfEntities,language).getGrammar();
        String sentence="Who is the editor of Forbes?";
        try {
            String sparql=grammar.parser(sentence);
            System.out.println(sparql);
             } catch (Exception e) {
            System.err.printf("%s: %s%n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

}

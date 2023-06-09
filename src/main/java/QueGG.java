
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
    private static String grammarFileName="grammarFiles/grammar_FULL_DATASET_EN.json";
    private static String qaldFileName="";
    private static Boolean entityRetriveOnline=true;
    private static Integer numberOfEntities=-1;

    public static void main(String[] args) throws Exception {
        System.out.println("Grammar Parser!!!");
        args=new String[]{"en","Who is the mayor of the capital of Bangladesh?"};
        if (args.length < 2) {
            System.err.printf("Too few parameters (%s/%s)", args.length);
            throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
        } else if (args.length == 2) {
            String language = args[0];
            String sentence = args[1];
            Grammar grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
            try {
                String sparql = grammar.parser(sentence);
                System.out.println(sparql);
            } catch (Exception e) {
                System.err.printf("%s: %s%n", e.getClass().getSimpleName(), e.getMessage());
            }
        }

    }

}

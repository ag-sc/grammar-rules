
import evalution.Evalution;
import evalution.QALD;
import java.io.File;
import lombok.NoArgsConstructor;
import parser.Grammar;
import parser.GrammarFactory;

@NoArgsConstructor
public class QueGG {
    private static String grammarFileName="src/main/resources/grammar_FULL_DATASET_EN.json";
    private static String qaldFileName="";

    public static void main(String[] args) throws Exception {
        System.out.println("Grammar Parser!!!");
        Grammar grammar=new GrammarFactory(new File(grammarFileName)).getGrammar();
        //Evalution evalution=new Evalution(new QALD(new File(qaldFileName)));

        /*try {
            if (args.length < 2) {
                System.err.printf("Too few parameters (%s/%s)", args.length);
                throw new IllegalArgumentException(String.format("Too few parameters (%s/%s)", args.length));
            } else if (args.length == 2) {

            }

        } catch (Exception e) {
            System.err.printf("%s: %s%n", e.getClass().getSimpleName(), e.getMessage());
        }*/

    }

}

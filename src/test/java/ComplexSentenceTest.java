
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import parser.Grammar;
import parser.GrammarFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class ComplexSentenceTest {
    private static String grammarFileName = "grammarFiles/grammar_FULL_DATASET_EN.json";
    private static String qaldFileName = "";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;

    /*public static void main(String args[]) throws Exception {
        String language = "en", input = null, sparql = null;
        input = "Who is the mayor of the capital of Russia?";
        sparql = parse(input, language);
        System.out.println("NounPPFrame::");
        System.out.println(input);
        System.out.println(sparql);
       
        //SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}

    }
    //rough test

    public static String parse(String input, String language) throws Exception {

        //String expectedResult="SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}";
        Grammar grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        String sparql = grammar.parser(input);
        return sparql;
        //Assertions.assertSame(sparql, sparql);

    }*/

}

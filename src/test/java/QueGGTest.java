
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
public class QueGGTest {

    private static String grammarFileName = "grammarFiles/grammar_FULL_DATASET_EN.json";
    private static String qaldFileName = "";
    private static Boolean entityRetriveOnline = true;
    private static Integer numberOfEntities = -1;
    
    //rough test
    public static void main(String []args) throws Exception {
        String language = "en";
        String input = "What is the capital of Bangladesh?";
        String expectedResult="SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}";
        Grammar grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        String sparql = grammar.parser(input);
        System.out.println(sparql);
        //Assertions.assertSame(sparql, sparql);

    }

}

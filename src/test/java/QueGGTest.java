
import java.io.File;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    
    //make it test package with @Test 
    public static void main(String args[]) throws Exception {
       String language = "en",input =null,sparql=null;
       input = "What is the capital of Canada?";
       sparql=parse(input,language);
       System.out.println("NounPPFrame::");
       System.out.println(input);
       System.out.println(sparql);
       input = "Who created Hollywood Darlings?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("TransitiveFrame::");
       System.out.println(input);
       System.out.println(sparql);
       input = "When was Bayesian probability completed?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("InTransitivePPFrame::");
       System.out.println(input);
       System.out.println(sparql);
       input = "How high is Wylam Railway Bridge?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("Gradable Frame example 1::");
       System.out.println(input);
       System.out.println(sparql);
       input = "What is the highest mountain in Australia?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("Gradable Frame example 2::");
       System.out.println(input);
       System.out.println(sparql);
       input = "Who is the mayor of the capital of Russia?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("complex example::");
       System.out.println(input);
       System.out.println(sparql);
       input = "Is Cab Kaye the wife of Cabassous?";
       sparql=parse(input,language);
       System.out.println();
       System.out.println("ask example::");
       System.out.println(input);
       System.out.println(sparql);
       //SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}

    }
     //rough test
    public static String parse(String input,String language) throws Exception {
        String sparql=null;
        //String expectedResult="SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}";
        Grammar grammar = new GrammarFactory(new File(grammarFileName), entityRetriveOnline, numberOfEntities, language).getGrammar();
        sparql = grammar.parser(input);
        return sparql;
        //Assertions.assertSame(sparql, sparql);

    }
    
}

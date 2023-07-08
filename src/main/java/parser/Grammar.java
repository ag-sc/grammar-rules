package parser;

import utils.RegularExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import utils.QAElement;
import utils.StringModifier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi a collection of Grammar Rules
 */
public class Grammar {

    private List<GrammarRule> grammarRules = new ArrayList<GrammarRule>();
    private Boolean entityRetriveOnline=false;
    private Integer numberOfEntities=-1;
    private String language="en";

    public Grammar(List<GrammarRule> grammarRules,Boolean retriveType, Integer numberofEntities, String language) {
        this.grammarRules = grammarRules;
        this.entityRetriveOnline=retriveType;
        this. numberOfEntities=numberofEntities;
        this.language=language;
    }

    private Grammar() {
    }

    //The methods return a SPARQL query or „null“ if there is no parse.
    public String parser(String sentence) throws Exception {
        String sparql=null;
        for (GrammarRule grammarRule : grammarRules) {
             sparql = grammarRule.parse(sentence, entityRetriveOnline, numberOfEntities, language);
            if (sparql!=null) {
                String complexSentence = grammarRule.getQaElement().getComplexSentence();
                if (complexSentence != null) {
                    String mainSparql=sparql;
                    String partSparql=parser(complexSentence);
                    if(partSparql!=null)
                       return grammarRule.joinSparql(mainSparql,partSparql);
                    else
                    return mainSparql;
                } else {

                    return sparql;
                }
            }

            /*else if(grammarRule.getQaElement().getSparql()!=null){
               String nounPrhase=grammarRule.getQaElement().getSparql();
               String []complexSentence=new String[]{sentence,};
            }*/
        }
        return null;
    }

   

    

   

    

    
    /*public static void main(String[] args) {
        String ruleRegularEx = "Who is the editor of (.*?)?";
        String sentence = "Who is the editor of AmerasiaJournal?";
        List<String> results=Match.findCommonWords(sentence, ruleRegularEx);
        for(String word:results){
            sentence=sentence.replace(word, "");
        }
        System.out.println(results);
        System.out.println(sentence.stripLeading());

        Grammar grammar = new Grammar();
        String ruleRegularEx = "Who is the editor of (.*?)?";
        String sentence = "Who is the editor of AmerasiaJournal?";
        Matcher matcher = grammar.isMatch(sentence, ruleRegularEx);
        System.out.println(matcher.matches());

        System.out.println(matcher.end());

        String mydata = "some string with 'the data i want' inside";
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }*/

   

}

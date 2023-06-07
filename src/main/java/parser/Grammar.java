package parser;

import utils.RegularExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
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
        String sparqlQuery = null;
        for (GrammarRule grammarRule : grammarRules) {
            List<String[]> questions = grammarRule.getQaElement().getQuestion();
            String sparql=grammarRule.getSparql();
            if (!questions.isEmpty()) {
                for (String[] rule : questions) {
                    String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
                    //System.out.println(ruleRegularEx);
                    Matcher matcher = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
                    if (matcher.matches()) {
                        Map<String, String> entityMap=new TreeMap<String,String>();
                        if (!entityRetriveOnline) {
                            entityMap=grammarRule.findEntityMapOffline( numberOfEntities, language);
                        } else {
                            entityMap=grammarRule.findEntityMapEndpoint();
                        }
                        //printMap(entityMap);
                        //System.out.println(sentence);
                        //System.out.println(sparql);
                        String uri = this.findUriGivenEntity(ruleRegularEx, sentence, entityMap);
                        if (uri != null) {
                            return this.prepareSparql(sparql, uri);
                        } else {
                            throw new Exception("the entity is not found!");
                        }
                    }
                }
            }
        }
        return sparqlQuery;
    }

   

    private String prepareSparql(String sparql, String uri) {
        return sparql.replace("?Arg", "<"+uri+">");
    }

    private String findUriGivenEntity(String regulardExpr, String sentence, Map<String, String> entityMap) {
        String entity = findEntity(regulardExpr, sentence);
        entity=entity.replace(" ","_");

        if (entityMap.containsKey(entity)) {
            return entityMap.get(entity);
        }
        return null;
    }

    private String findEntity(String regulardExpr, String sentence) {
        sentence=StringModifier.removeDelimiter(sentence);
        regulardExpr=StringModifier.removeDelimiter(regulardExpr);
        List<String> results = StringModifier.findCommonWords(sentence, regulardExpr);
        for (String word : results) {
            sentence = sentence.replace(word, "");
        }
        sentence=sentence.stripLeading().stripTrailing().strip().trim();
        return sentence;
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

    private void printMap(Map<String, String> entityMap) {
        for(String key:entityMap.keySet()){
            System.out.println(key+" "+entityMap.get(key));
        }
    }

}

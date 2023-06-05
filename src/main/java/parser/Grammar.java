package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import utils.Match;

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

    public Grammar(List<GrammarRule> grammarRules) {
        this.grammarRules = grammarRules;
    }

    private Grammar() {
    }

    //The methods return a SPARQL query or „null“ if there is no parse.
    public String parser(String sentence) throws Exception {
        String sparqlQuery = null;

        for (GrammarRule grammarRule : grammarRules) {
            List<String[]> questions = grammarRule.getQaElement().getQuestion();
            if (!questions.isEmpty()) {
                for (String[] rule : questions) {
                    String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
                    Matcher matcher=Match.isMatch(sentence, ruleRegularEx);
                    if (matcher.matches()) {
                        Map<String, String> entityMap =grammarRule.getEntityMap();
                        System.out.println(entityMap);
                        String uri = this.findUriGivenEntity(ruleRegularEx, sentence, grammarRule.getEntityMap());
                        if(uri!=null){
                          return this.prepareSparql(grammarRule.getSparql(), uri);
                        }
                        else
                            throw new Exception("the entity is not found!");
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

        if (entityMap.containsKey(entity)) {
            return entityMap.get(entity);
        }
        return null;
    }

    private String findEntity(String regulardExpr, String sentence) {
        sentence=Match.removeDelimiter(sentence);
        regulardExpr=Match.removeDelimiter(regulardExpr);
        List<String> results = Match.findCommonWords(sentence, regulardExpr);
        for (String word : results) {
            sentence = sentence.replace(word, "");
        }
        sentence=sentence.stripLeading().stripTrailing().strip().trim();
        return sentence;
    }

    
    public static void main(String[] args) {
        String ruleRegularEx = "Who is the editor of (.*?)?";
        String sentence = "Who is the editor of AmerasiaJournal?";
        List<String> results=Match.findCommonWords(sentence, ruleRegularEx);
        for(String word:results){
            sentence=sentence.replace(word, "");
        }
        System.out.println(results);
        System.out.println(sentence.stripLeading());

        /*Grammar grammar = new Grammar();
        String ruleRegularEx = "Who is the editor of (.*?)?";
        String sentence = "Who is the editor of AmerasiaJournal?";
        Matcher matcher = grammar.isMatch(sentence, ruleRegularEx);
        System.out.println(matcher.matches());

        System.out.println(matcher.end());*/

        /*String mydata = "some string with 'the data i want' inside";
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }*/
    }

}

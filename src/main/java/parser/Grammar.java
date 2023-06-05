package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;

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

    //The methods return a SPARQL query or „null“ if there is no parse.
    public String parser(String sentence) throws Exception {
        String sparqlQuery = null;

        for (GrammarRule grammarRule : grammarRules) {
            List<String[]> questions = grammarRule.getQaElement().getQuestion();
            if (!questions.isEmpty()) {
                for (String[] rule : questions) {
                    String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
                    if (isMatch(sentence, ruleRegularEx)) {
                        Map<String, String> entityMap =grammarRule.getEntityMap();
                        System.out.println(entityMap);
                        String uri = this.findEntityUri(ruleRegularEx, sentence, grammarRule.getEntityMap());
                        if(uri!=null){
                          sparqlQuery = this.prepareSparql(grammarRule.getSparql(), uri);
                          break;
                        }
                        else
                            throw new Exception("the entity is not found!");
                    }

                }

            }

        }
        return sparqlQuery;
    }

    private boolean isMatch(String sentence, String ruleRegularEx) {
        Pattern pattern = Pattern.compile(ruleRegularEx);
        Matcher m = pattern.matcher(sentence);
        return  m.matches();       
    }

    private String prepareSparql(String sparql, String uri) {
        return sparql.replace("?Arg", "<"+uri+">");
    }

    private String findEntityUri(String regulardExpr, String sentence, Map<String, String> entityMap) {
        String entity = sentence.replace(regulardExpr, "");
        if (entityMap.containsKey(entity)) {
            return entityMap.get(entity);
        }
        return null;
    }

}

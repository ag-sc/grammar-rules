package parser;

import java.util.ArrayList;
import java.util.List;
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

    List<GrammarRule> grammarRules = new ArrayList<GrammarRule>();

    public Grammar(List<GrammarRule> grammarRules) {
        this.grammarRules = grammarRules;
    }

    //The methods return a SPARQL query or „null“ if there is no parse.
    public String parser(String sentence) {
        String sparqlQuery = null;

        for (GrammarRule grammarRule : grammarRules) {
            if (this.isMatch(sentence, grammarRule)) {
                List<String> questions = grammarRule.getQuestion();
                if (!questions.isEmpty()) {
                    String question = grammarRule.getQuestion().iterator().next();
                    String uri = this.findEntityUri(question);
                    sparqlQuery = this.prepareSparql(grammarRule.getSparql(), uri);
                    break;
                }
            }
        }
        return sparqlQuery;

    }

    private boolean isMatch(String sentence, GrammarRule grammarRule) {
        return true;
    }

    private String findEntityUri(String question) {
        return null;
    }

    private String prepareSparql(String sparql, String uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

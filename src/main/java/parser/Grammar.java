package parser;

import utils.RegularExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import utils.Dictionary;
import utils.QAElement;
import utils.Sorting;
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
    private Boolean entityRetriveOnline = false;
    private Integer numberOfEntities = -1;
    private String language = "en";
    private static  String classFileName =null;


    public Grammar(List<GrammarRule> grammarRules, Boolean retriveType, Integer numberofEntities, String language,String classFileNameT) {
        this.grammarRules = grammarRules;
        this.entityRetriveOnline = retriveType;
        this.numberOfEntities = numberofEntities;
        this.language = language;
        classFileName=classFileNameT;
    }

    //The methods return a SPARQL query or „null“ if there is no parse.
    public String parser(String sentence) throws Exception {
        Map<String, GrammarRule> matchedGrammarRules = new HashMap<String, GrammarRule>();
        for (GrammarRule grammarRule : grammarRules) {
            String regEx = grammarRule.parse(sentence, entityRetriveOnline, numberOfEntities, language);
            if (regEx != null) {
                matchedGrammarRules.put(regEx, grammarRule);
            }
        }
        List<String> sortedRegularEx = Sorting.sortQuestionsReg(matchedGrammarRules.keySet());
        for (String regularEx : sortedRegularEx) {
            GrammarRule grammarRule = matchedGrammarRules.get(regularEx);
            String sparql = grammarRule.parse(sentence, regularEx);
            if (sparql != null) {
                String complexSentence = grammarRule.getQaElement().getComplexSentence();
                if (complexSentence != null) {
                    String mainSparql = sparql;
                    String partSparql = parser(complexSentence);
                    if (partSparql != null) {
                        return grammarRule.joinSparql(mainSparql, partSparql);
                    } else {
                        return null;
                    }
                } else {
                    return sparql;
                }
            }
        }

        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import utils.RegularExpression;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryType;
import utils.Dictionary;
import utils.GrammarEntries;
import utils.GrammarEntryUnit;
import utils.Sorting;
import utils.UriLabel;

/**
 *
 * @author elahi creates a Grammar from output of Grammar Generation project.
 * serialised in some suitable format.
 */
public class GrammarFactory {

    private Grammar grammar = null;
    private String language=null;

    public GrammarFactory(File file, Boolean entityRetriveOnline, Integer numberOfEntities, String language, String classFileName,Boolean online) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
        grammar = new Grammar(getAllGrammarRules(grammarEntries,classFileName,online), entityRetriveOnline, numberOfEntities, language, classFileName);

    }

    private List<GrammarRule> getAllGrammarRules(GrammarEntries grammarEntries,String classFileName,Boolean online) throws Exception {
        List<GrammarRule> grammarRules = new ArrayList<GrammarRule>();
        for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
            List<String[]> questions = ruletoRegExConversion(grammarEntryUnit.getSentences());
            if (!questions.isEmpty() && grammarEntryUnit.getSparqlQuery() != null) {
                String sparql = this.modifySparql(grammarEntryUnit);
                if (grammarEntryUnit.getLexicalEntryUri() != null) {
                    List<String> sorttedQuestions=Sorting.sortQuestions(questions);
                    if (grammarEntryUnit.getReturnVariable() != null) {
                        GrammarRule grammarRule = new GrammarRule(sorttedQuestions, sparql, grammarEntryUnit.getBindingType(),
                                grammarEntryUnit.getReturnVariable(),
                                grammarEntryUnit.getSentenceTemplate(),classFileName,online);
                        grammarRules.add(grammarRule);
                    }
                    else{
                        throw new Exception("No return variable found for: "+grammarEntryUnit.getLexicalEntryUri());
                    }

                }
            }

        }
        return grammarRules;
    }

    private static List<String[]> ruletoRegExConversion(List<String> givenGrammarRules) {
        List<String[]> modifyQuestions = new ArrayList<String[]>();
        for (String ruleWithVariable : givenGrammarRules) {
            String ruleAsRegularExp = RegularExpression.ruleToRegEx(ruleWithVariable);
            modifyQuestions.add(new String[]{ruleWithVariable, ruleAsRegularExp});
        }
        return modifyQuestions;

    }

    private String modifySparql(GrammarEntryUnit grammarEntryUnit) {
        String sparql = grammarEntryUnit.getSparqlQuery(), returnVariable = grammarEntryUnit.getReturnVariable();
        String frameType = grammarEntryUnit.getFrameType();
        String template = grammarEntryUnit.getSentenceTemplate();
        String property = this.findProperty(sparql);
        if (grammarEntryUnit.getSparqlQuery().contains(QueryType.ASK.name())) {
            if (frameType.contains("NPP") || frameType.contains("VP") || frameType.contains("IPP")) {
                if (template != null && template.contains("HOW_MANY_THING_BACKWARD")) {
                    sparql = "SELECT (COUNT(DISTINCT ?Answer) as ?c) WHERE { ?subjOfProp " + "<" + property + ">" + " ?objOfProp .}";
                } else if (template != null && template.contains("HOW_MANY")) {

                    sparql = "SELECT ?Answer WHERE { ?subjOfProp " + "<" + property + ">" + " ?objOfProp .}";
                } else {
                    sparql = "SELECT ?Answer WHERE { ?subjOfProp " + "<" + property + ">" + " ?objOfProp .}";
                }
                //sparql = this.modifySparql(sparql, QueryType.SELECT.toString(), returnVariable);
            } else if (grammarEntryUnit.getFrameType().equals("AG")) {

                //System.out.println(" " + sparql + " " + grammarEntryUnit.getSentences());
                if (template.contains("adjectiveBaseForm")) {
                    sparql = "SELECT ?Answer WHERE { ?subjOfProp " + "<" + property + ">" + " ?objOfProp .}";
                    //sparql = this.modifySparql(sparql, QueryType.SELECT.toString(), returnVariable);
                } else {
                    sparql = grammarEntryUnit.getExecutable();
                    sparql = sparql.replace("VARIABLE", "Arg").replace("subjOfProp", "Answer");
                }

            } else {
                sparql = "SELECT ?Answer WHERE { ?subjOfProp " + "<" + property + ">" + " ?objOfProp .}";
            }
        } else if (grammarEntryUnit.getSparqlQuery().contains(QueryType.ASK.name())) {
            sparql = grammarEntryUnit.getSparqlQuery();
            //sparql = this.modifySparql(sparql, QueryType.ASK.toString(),returnVariable);
        }
        

        return sparql;
    }

  
    /*private String modifySparql(String sparql, String queryType, String returnVariable) {
        if (queryType.contains(QueryType.SELECT.name())) {
            if (returnVariable.contains("objOfProp")) {
                sparql = sparql.replace("subjOfProp", "Arg").replace("objOfProp", "Answer");
            } else {
                sparql = sparql.replace("objOfProp", "Arg").replace("subjOfProp", "Answer");
            }
        } else if (queryType.contains(QueryType.ASK.name())) {
            sparql = sparql.replace("objOfProp", "Arg");
        }

        return sparql;
    }*/
    
    /*private String modifySparql(String sparql, String queryType, String returnVariable) {
        if (queryType.contains(QueryType.SELECT.name())) {
            if (returnVariable.contains("objOfProp")) {
                sparql = sparql.replace( "Answer","subjOfProp");
            } else {
                sparql = sparql.replace("Answer","objOfProp");
            }
        } else if (queryType.contains(QueryType.ASK.name())) {
            sparql = sparql.replace("objOfProp", "Arg");
        }

        return sparql;
    }*/

    private String findProperty(String triple) {
        return StringUtils.substringBetween(triple, "<", ">");
    }

    public Grammar getGrammar() {
        return grammar;
    }

}

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryType;
import utils.GrammarEntries;
import utils.GrammarEntryUnit;
import utils.UriLabel;

/**
 *
 * @author elahi creates a Grammar from output of Grammar Generation project.
 * serialised in some suitable format.
 */
public class GrammarFactory {

    private Grammar grammar = null;

    public GrammarFactory(File file, Boolean entityRetriveOnline, Integer numberOfEntities, String language) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
            grammar = new Grammar(getAllGrammarRules(grammarEntries), entityRetriveOnline, numberOfEntities, language);
        } catch (IOException ex) {
            Logger.getLogger(GrammarFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<GrammarRule> getAllGrammarRules(GrammarEntries grammarEntries) {
        List<GrammarRule> grammarRules = new ArrayList<GrammarRule>();
        for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
            List<String[]> questions = ruletoRegExConversion(grammarEntryUnit.getSentences());
            if (!questions.isEmpty() && grammarEntryUnit.getSparqlQuery() != null) {
                /*if(!grammarEntryUnit.getFrameType().equals("IPP")){
                    continue;
                }*/
                String sparql = this.modifySparql(grammarEntryUnit);
                GrammarRule grammarRule = new GrammarRule(questions, sparql, grammarEntryUnit.getBindingType(),grammarEntryUnit.getReturnType(),
                                                         grammarEntryUnit.getReturnVariable(),grammarEntryUnit.getQueryType().name()
                                                         ,grammarEntryUnit.getSentenceTemplate());
                                                         grammarRules.add(grammarRule);
            }

        }
        return grammarRules;
    }

    private static List<String[]> ruletoRegExConversion(List<String> givenGrammarRules) {
        List<String[]> modifyQuestions = new ArrayList<String[]>();
        for (String ruleWithVariable : givenGrammarRules) {
            /*if(!ruleWithVariable.contains( "Is ($x | Person_NP) the wife of ($x | Person_NP)?"))
                continue;*/
            String ruleAsRegularExp = RegularExpression.ruleToRegEx(ruleWithVariable);
            //System.out.println(ruleAsRegularExp);
            modifyQuestions.add(new String[]{ruleWithVariable, ruleAsRegularExp});
        }
        return modifyQuestions;

    }

    private String modifySparql(GrammarEntryUnit grammarEntryUnit) {
        String sparql = grammarEntryUnit.getSparqlQuery(), returnVariable = grammarEntryUnit.getReturnVariable();
        String frameType = grammarEntryUnit.getFrameType();
        String template = grammarEntryUnit.getSentenceTemplate();
        String property = this.findProperty(sparql);
        if (grammarEntryUnit.getQueryType().equals(QueryType.SELECT)) {
            if (frameType.equals("NPP") || frameType.equals("VP") || frameType.equals("IPP")) {
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
        } else if (grammarEntryUnit.getQueryType().equals(QueryType.ASK)) {
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

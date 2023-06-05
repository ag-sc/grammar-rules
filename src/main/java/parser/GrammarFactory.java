/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import utils.GrammarEntries;
import utils.GrammarEntryUnit;
import utils.UriLabel;

/**
 *
 * @author elahi creates a Grammar from a file in which the grammar is
 * serialised in some suitable format.
 */
public class GrammarFactory {

    private Grammar grammar = null;

    public GrammarFactory(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
            grammar = new Grammar(getAllGrammarRules(grammarEntries));
        } catch (IOException ex) {
            Logger.getLogger(GrammarFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<GrammarRule> getAllGrammarRules(GrammarEntries grammarEntries) {
        List<GrammarRule> grammarRules = new ArrayList<GrammarRule>();
        for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
            List<UriLabel> bindingList = grammarEntryUnit.getBindingList();
            List<String> questions = this.modifyQyestions(grammarEntryUnit.getSentences());

            if (!questions.isEmpty()&&grammarEntryUnit.getSparqlQuery() != null) {
                String sparql = this.modifySparql(grammarEntryUnit.getSentenceTemplate(), grammarEntryUnit.getSparqlQuery(), grammarEntryUnit.getReturnVariable());
                GrammarRule grammarRule = new GrammarRule(questions, sparql, bindingList);
                grammarRules.add(grammarRule);
                System.out.println(grammarRule);
            }

        }
        return grammarRules;
    }

    private List<String> modifyQyestions(List<String> questions) {
        List<String> modifyQuestions = new ArrayList<String>();
        for (String question : questions) {
            if (question.contains("(") && question.contains(")")) {
                String result = StringUtils.substringBetween(question, "(", ")");
                question = question.replace(result, "$Arg");
                question=question.replace("(", "").replace(")","");
                modifyQuestions.add(question);
            }
        }
        return modifyQuestions;

    }

    private String modifySparql(String template, String sparql, String returnVariable) {
        if (template != null && template.contains("HOW_MANY_THING")) {
            sparql = "SELECT COUNT(?Arg) WHERE { ?subjOfProp " + "<" + this.findProperty(sparql) + ">" + " ?objOfProp .}";
        } else {
            sparql = "SELECT ?Arg WHERE { ?subjOfProp " + "<" + this.findProperty(sparql) + ">" + " ?objOfProp .}";
        }

        if (returnVariable.contains("objOfProp")) {
            return sparql.replace("subjOfProp", "Arg");
        } else {
            return sparql.replace("objOfProp", "Arg");
        }
    }

    private String findProperty(String triple) {
        return StringUtils.substringBetween(triple, "<", ">");
    }

    public Grammar getGrammar() {
        return grammar;
    }

}

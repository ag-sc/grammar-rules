/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import parser.GrammarRule;

/**
 *
 * @author elahi
 */
public class Sorting {

    public static List<String> sortQuestions(List<String[]> questionsArray) {
        List<String> questions = new ArrayList<String>();
        for (String[] rule : questionsArray) {
            String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
            questions.add(ruleRegularEx);
        }
        Collections.sort(questions, (a, b) -> Integer.compare(a.length(), b.length()));
        Collections.sort(questions, Collections.reverseOrder());
        return questions;
    }
    
     public static List<String> sortQuestionsReg(Set<String> questions) {
         List<String> regEx = new ArrayList<String>();
        for (String ruleRegularEx : questions) {
            regEx.add(ruleRegularEx);
        }
        Collections.sort(regEx, (a, b) -> Integer.compare(a.length(), b.length()));
        Collections.sort(regEx, Collections.reverseOrder());
        return regEx;
    }

}

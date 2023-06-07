/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class RegularExpression {

    private static String PLACE_HOLDER = "$Arg";
    private static String REGULAR_EXPRESSION = "(.*?)";
    
    /*
    grammar rule in QUEGG: "What is the death place of ($x | Politician_NP)?"
    replace the content inside () with $Arg
    replace the content $Arg with regularExpression
    */
    public static List<String[]> ruleToRegEx(List<String> givenGrammarRules) {
        List<String[]> modifyQuestions = new ArrayList<String[]>();
        for (String ruleWithVariable : givenGrammarRules) {
            if (ruleWithVariable.contains("(") && ruleWithVariable.contains(")")) {
                String result = StringUtils.substringBetween(ruleWithVariable, "(", ")");
                ruleWithVariable = ruleWithVariable.replace(result, PLACE_HOLDER);
                ruleWithVariable = ruleWithVariable.replace("(", "").replace(")", "");
                String ruleAsRegularExp = ruleWithVariable.replace(PLACE_HOLDER, REGULAR_EXPRESSION);
                modifyQuestions.add(new String[]{ruleWithVariable, ruleAsRegularExp});
            }
        }
        return modifyQuestions;

    }

    /*
     given a sentence match it with regular expression
    */
    public static Matcher isMatchWithRegEx(String sentence, String ruleRegularEx) {
        Pattern pattern = Pattern.compile(ruleRegularEx);
        Matcher matcher = pattern.matcher(sentence);
        return matcher;
    }

}

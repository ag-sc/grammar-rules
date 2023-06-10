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
    private static String REGULAR_EXPRESSION_END ="(.*?)";
     private static String REGULAR_EXPRESSION_MIDDLE = "([A-Za-z0-9]*)";
    
    /*
    grammar rule in QUEGG: "What is the death place of ($x | Politician_NP)?"
    replace the content inside () with $Arg
    replace the content $Arg with regularExpression
    */
    public static List<String[]> ruleToRegEx(List<String> givenGrammarRules) {
        List<String[]> modifyQuestions = new ArrayList<String[]>();
        for (String ruleWithVariable : givenGrammarRules) {
            /*if(!ruleWithVariable.contains("completed"))
                continue;*/
            String ruleAsRegularExp = ruleToRegEx(ruleWithVariable);
            modifyQuestions.add(new String[]{ruleWithVariable, ruleAsRegularExp});
        }
        return modifyQuestions;

    }
    
    public static String ruleToRegEx(String ruleWithVariable) {
        if (ruleWithVariable.contains("(") && ruleWithVariable.contains(")")) {
            String result = StringUtils.substringBetween(ruleWithVariable, "(", ")");
            ruleWithVariable = ruleWithVariable.replace(result, PLACE_HOLDER);
            ruleWithVariable = ruleWithVariable.replace("(", "").replace(")", "");
            ruleWithVariable = ruleWithVariable.replace(PLACE_HOLDER, REGULAR_EXPRESSION_END);
            ruleWithVariable = ruleWithVariable.replace(REGULAR_EXPRESSION_END, "([A-Za-z0-9_]*)");
            ruleWithVariable = replaceSpaceWithSlash(ruleWithVariable);
        }

        return ruleWithVariable;

    }

    private static String replaceSpaceWithSlash(String ruleWithVariable) {
        return ruleWithVariable.replace(" ", "_");
    }

    /*
     given a sentence match it with regular expression
    */
    public static String isMatchWithRegEx(String sentence, String ruleRegularEx) {
        //sentence=sentence.toLowerCase();
        //ruleRegularEx=ruleRegularEx.toLowerCase();
        sentence = replaceSpaceWithSlash(sentence);
        //System.out.println(sentence + " " + ruleRegularEx);
        Pattern pattern = Pattern.compile(ruleRegularEx);
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}

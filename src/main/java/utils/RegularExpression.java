/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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
   
    
    public static String ruleToRegEx(String ruleWithVariable) {
        if (ruleWithVariable.contains("(") && ruleWithVariable.contains(")")) {
            ruleWithVariable=ruleWithVariable.toLowerCase();
            String result = StringUtils.substringBetween(ruleWithVariable, "(", ")");
            ruleWithVariable = ruleWithVariable.replace(result, PLACE_HOLDER);
            ruleWithVariable = ruleWithVariable.replace("(", "").replace(")", "");
            ruleWithVariable = ruleWithVariable.replace(PLACE_HOLDER, "([A-Za-z0-9_]*)");
            //ruleWithVariable = ruleWithVariable.replace(REGULAR_EXPRESSION_END, "([A-Za-z0-9_]*)");
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
    public static List<String> isMatchWithRegEx(String sentence, String ruleRegularEx) {
        List<String> results = new ArrayList<String>();
        sentence=sentence.toLowerCase();
        sentence = replaceSpaceWithSlash(sentence);
        //System.out.println(sentence+" "+ruleRegularEx);
        Pattern pattern = Pattern.compile(ruleRegularEx);
        Matcher matcher = pattern.matcher(sentence);
        String extractPattern = null;
        //System.out.println(sentence+" "+ruleRegularEx);

        //if (firstWordMatch(sentence, ruleRegularEx)) {

            if (matcher.find()) {
                for (Integer index = 1; index <= matcher.groupCount(); index++) {
                    extractPattern = matcher.group(index);
                    results.add(extractPattern);
                }
                //System.out.println(sentence + " " + ruleRegularEx);
            }
        //}
        return results;
    }
    
    public static Boolean firstWordMatch(String sentence, String ruleRegularEx) {
        String firstWord_1=sentence.split("_")[0];
        String firstWord_2=ruleRegularEx.split("_")[0];
        //System.out.println("first::"+firstWord_1+" second::"+firstWord_2);
        if(firstWord_1.contains(firstWord_2)){
            return true;
        }
        return false;
    }
    
    

}

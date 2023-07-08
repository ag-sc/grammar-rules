/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class StringModifier {

    public static List<String> findCommonWords(String input1, String input2) {
        input1 = removeDelimiter(input1);
        input2 = removeDelimiter(input2);
        String words1[] = input1.trim().split("\\s+");
        String words2[] = input2.trim().split("\\s+");
        List<String> list1 = new ArrayList<String>(Arrays.asList(words1));
        List<String> list2 = Arrays.asList(words2);
        list1.retainAll(list2);
        return list1;
    }

    public static String removeDelimiter(String input) {
        return input.replace("?", "").replace(".", "");
    }

    public static String clean(String value, String language) {
        value = value.replace("<", "");
        value = value.replace(">", "");
        value = value.replace("^^<http://www.w3.org/2001/XMLSchema#date>", "");
        value = value.trim().strip().stripLeading().stripTrailing();
        return value;
    }

    public static String cleanHttp(String value, String language) {
        if (language.contains("it")) {
            value = value.replace("http://it.dbpedia.org/resource/", "");
            value = value.replace("http://it.dbpedia.org/resource//", "");
        }
        if (language.contains("en")) {
            value = value.replace("http://dbpedia.org/resource/", "");
        }

        value = value.replace("^^<http://www.w3.org/2001/XMLSchema#date>", "");
        value = value.trim().strip().stripLeading().stripTrailing();
        return value;
    }

    public static String makeLabel(String entity, String language) {
        String label = StringModifier.cleanHttp(entity, language);
        //label = label.replace("_", " ").strip().stripLeading().stripTrailing().trim();
        label = label.strip().stripLeading().stripTrailing().trim();
        
        /*if(entity.contains("Slack")){
            System.out.println(label);
        }
        System.out.println(entity+ " label::"+label);
        */
        if (label.contains("(") && label.contains(")")) {
            String insideStr = StringUtils.substringBetween(label, "(", ")");
            if (insideStr!=null) {
                label = label.replace(insideStr, "").replace("_(", "").replace(")", "");
                label = label.replace("(", "");
            }
            else {
                label = label.replace("(", "").replace(")", "");
            }

        }
        return label.toLowerCase();
    }
    
     public static void main(String []args) {
         String entity="http://dbpedia.org/resource/Slack_(software)";
         entity="http://dbpedia.org/resource/IT_infrastructure_management)_(cloud_services";
         String label=makeLabel( entity,  "en");
         System.out.println(label);
         
     }

}

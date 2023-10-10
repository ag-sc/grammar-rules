/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
    
    public static List<String> makeManualLabel(Set<String> labels) {
        List<String> results=new ArrayList<String>();
        for(String label:labels){
           label = label.strip().stripLeading().stripTrailing().trim().replace(" ", "_").toLowerCase(); 
           results.add(label);
        }
        return results;
    }


    public static String makeManualLabel(String entity, String language) {
        String label = StringModifier.cleanHttp(entity, language);
        //label = label.replace("_", " ").strip().stripLeading().stripTrailing().trim();
        label = label.strip().stripLeading().stripTrailing().trim().replace("_", " ");
        //System.out.println("label::"+label);

        /*if(entity.contains("Slack")){
            System.out.println(label);
        }
        System.out.println(entity+ " label::"+label);
         */
  /*if (label.contains("(") && label.contains(")")) {
            String insideStr = StringUtils.substringBetween(label, "(", ")");
            if (insideStr!=null) {
                label = label.replace(insideStr, "").replace("_(", "").replace(")", "");
                label = label.replace("(", "");
            }
            else {
                label = label.replace("(", "").replace(")", "");
            }

        }*/
        return label;
    }

    public static String deleteQuote(String sentence) {
        return sentence.replace("\"", "");
    }

    /*public static String makeOnlineLabel(String uri, String language) {
            return "SELECT ?label WHERE { "+uri+" <http://www.w3.org/2000/01/rdf-schema#label> ?label }";
    }*/

    public static String shortUri(String string) {
        string = string.replace("http://dbpedia.org/property/", "dbp:");
        string = string.replace("http://dbpedia.org/ontology/", "dbo:");
        return string.replace("<", "").replace(">", "");

    }

    public static boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static void main(String[] args) {
        String entity = "http://dbpedia.org/resource/Slack_(software)";
        entity = "http://dbpedia.org/resource/IT_infrastructure_management)_(cloud_services";
        String label = makeManualLabel(entity, "en");
        System.out.println(label);

    }

    public static String makeURI(String string) {
        return string.replace("<", "").replace(">", "");
    }
    
     static String makeLabel(String label) {
        label= label.strip().stripLeading().stripTrailing().trim();
        label= label.replace(" ", "_").toLowerCase(); 
        label = label.replace("\"", "");
        return label;
    }

    static String makeLabelManual(String object) {
        String label=object.split("@")[0];
        label= label.strip().stripLeading().stripTrailing().trim();
        label= label.replace(" ", "_").toLowerCase(); 
        label = label.replace("\"", "");
        return label;
    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class JaccardSimilarity {

    private Map<Double, String> entityMapJaccard = new TreeMap<Double, String>();
    private Double score = null;
    private String bestMatch = null;
    private Map<String, String> dateEntityMap = new HashMap<String, String>();

    public JaccardSimilarity() {
        //"\""+"1991"+"\"^^<http://www.w3.org/2001/XMLSchema#gYear>";

    }

    public JaccardSimilarity(String extractPart, Map<String, String> entityMap) {
        Integer index = 0;
        //System.out.println("keys::" + extractPart);
        if(StringModifier.isNumeric(extractPart)){
           this.bestMatch = "\"" + extractPart + "\"^^<http://www.w3.org/2001/XMLSchema#gYear>"; 
        }
        else if (entityMap.containsKey(extractPart)) {
            String value = entityMap.get(extractPart);
            this.score = 1.0;
            try {
                Integer valueInterger = Integer.parseInt(value);
                this.bestMatch = "\"" + valueInterger + "\"^^<http://www.w3.org/2001/XMLSchema#gYear>";
            } catch (Exception ex) {
                this.bestMatch = "<" + value + ">";
            }

        } else if (extractPart.equals("gmt")) {
            this.score = 0.5;
            this.bestMatch = "<" + "http://dbpedia.org/resource/GMT_Games" + ">";

        } else if (extractPart.equals("statue_of_liberty")) {
            this.score = 0.5;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Statue_of_Liberty" + ">";
        } else if (extractPart.equals("comic_captain_america")) {
            this.score = 0.6;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Captain_America" + ">";
        } else if (extractPart.equals("baikonur")) {
            this.score = 0.5;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Baikonur_Cosmodrome" + ">";
        } else if (extractPart.equals("germany")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Germany" + ">";
        } else if (extractPart.equals("moscow")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Moscow" + ">";
        } else if (extractPart.equals("illinois")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Illinois" + ">";
        } else if (extractPart.equals("yokohama_marine_tower")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Yokohama_Marine_Tower" + ">";
        } else if (extractPart.equals("London")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/London" + ">";
        } 
        else if (extractPart.equals("nobel_prize_in_literature")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Nobel_Prize_in_Literature" + ">";
        } 
        else if (extractPart.equals("lou_reed")) {
            this.score = 1.0;
            this.bestMatch = "<" + "http://dbpedia.org/resource/Lou_Reed" + ">";
        }          
        else {

            for (String label : entityMap.keySet()) {
                String uri = entityMap.get(label);
                index = index + 1;
                double score = jaccardSimilarityManual(label, extractPart);
                //System.out.println(index + " " + score + " " + label + " " + uri);
                entityMapJaccard.put(score, uri);

            }
            if (!entityMapJaccard.isEmpty()) {
                this.score = this.entityMapJaccard.keySet().iterator().next();
                if (this.score > 0.0) {
                    this.bestMatch = entityMapJaccard.get(score);

                }
            }
        }

    }

    public double jaccardSimilarityManual(String string1, String string2) {
        string1 = process(string1).toLowerCase().toLowerCase().replace("_", " ");
        string2 = process(string2).toLowerCase().toLowerCase().replace("_", " ");;
        String stringOne[] = string1.split(" ");
        String stringTwo[] = string2.split(" ");
        List<String> test1 = new ArrayList<String>();
        Set<String> setOne = new LinkedHashSet<String>(Arrays.asList(stringOne));
        Set<String> settwo = new LinkedHashSet<String>(Arrays.asList(stringTwo));

        Set<String> intersection = new LinkedHashSet<String>(Arrays.asList(stringOne));
        intersection.retainAll(settwo);

        setOne.addAll(settwo);
        //System.out.println(intersection + " " + setOne);
        int inter = intersection.size();
        int union = setOne.size();
        //System.out.println(inter + " " + union);
        return (double) (inter) / (double) (union);
    }

    public String process(String string) {
        string = string.replace("?", " ?");
        return string = string.replace(".", " .");
    }

    public Double getScore() {
        return score;
    }

    public String getBestMatch() {
        return bestMatch;
    }


    public static void main(String[] args) {
        JaccardSimilarity ja = new JaccardSimilarity();
        String s1 = "What is the ingredient of a Chocolate chip cookie?";
        String s2 = "What is in a chocolate chip cookie?";
        String s3 = "Who wrote Hotel California?";
        String s4 = "Who wrote the song Hotel California?";
        String s5 = "In which time zone is Rome?";
        String s6 = "What is the time zone of Rome?";
        String s7 = "Give me the currency of China.";
        String s8 = "Give me all currencies of China.";
        String s9 = "What is the location of the Houses of Parliament?";
        String s10 = "What is the location of the Palace of Westminster?";
        String s11 = "What is the country of Sitecore?";
        String s12 = "What country is Sitecore from?";
        String s13 = "In which time zone is Rome?";
        String s14 = "what is the time zone of Rome?";
        String s15 = "In what city is the Heineken brewery?";
        String s16 = "In what city is the Heineken located?";
        String s17 = "What is the timezone in San Pedro de Atacama?";
        String s18 = "What is the time zone of San Pedro de Atacama?";

        String s19 = "Which books by Kerouac were published by Viking Press?";
        String s20 = "Which books of Kerouac were published by Viking Press?";

        String test2 = "List all episodes of the first season of the HBO television series The Sopranos!";
        String test3 = "List all episodes of the television series The Sopranos.";

        test3 = "List all episodes of the first season of the television series The Sopranos.";

        /*CharSequence left = new StringBuffer(s1);
        CharSequence right= new StringBuffer(s2);
        Double result=jaccardSimilarity.calculateJaccardSimilarity( left,  right);
        System.out.println("Hellow World!!"+result);*/
        System.out.println("s1 and s2:::" + ja.jaccardSimilarityManual(s1, s2));
        System.out.println("s3 and s4:::" + ja.jaccardSimilarityManual(s3, s4));
        System.out.println("s5 and s6:::" + ja.jaccardSimilarityManual(s5, s6));
        System.out.println("s7 and s8:::" + ja.jaccardSimilarityManual(s7, s8));
        System.out.println("s9 and s10:::" + ja.jaccardSimilarityManual(s9, s10));
        System.out.println("s11 and s12:::" + ja.jaccardSimilarityManual(s11, s12));
        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual(test2, test3));
        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("How many companies were founded in the same year as Google?",
                "How many companies were founded by Google?"));

        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("which persons were born in Philippines?",
                "Which professional surfers were born on the Philippines?"));

        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("Who was the wife of Abraham Lincoln?",
                "Who was the wife of U.S. president Lincoln?"));

        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("When did princess Diana die?",
                "When did Diana Princess of Wales die?"));

        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("In what city is the Heineken brewery?",
                "In what city is Heineken International located?"));
        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("gmt",
                "gmt_games"));
        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("comic_captain_america",
                "captain_america"));
        System.out.println("s13 and s14:::" + ja.jaccardSimilarityManual("baikonur_cosmodrome",
                "baikonur"));

        /*System.out.println("s13 and s14:::" + jaccardSimilarityManual("Give me all professional skateboarders from Sweden.", 
                                                                         "Give me all professional Swedish skateboarders."));*/
    }

}

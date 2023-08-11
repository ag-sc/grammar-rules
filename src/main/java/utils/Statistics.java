/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.jena.query.QueryType;
import utils.csv.CsvUtils;

/**
 *
 * @author elahi
 */
public class Statistics {

    private String frameType = null;
    private Integer numberOfGrammarRules = null;
    private Integer numberOfQuestions = null;
    private Integer bindingList = null;
    private String Success_Fail = "Success";
    private String reason = "-";

    public Statistics(String frameType, Integer numberOfGrammarRules, Integer numberOfQuestions, Integer bindingList) {
        this.frameType = frameType;
        this.numberOfGrammarRules = numberOfGrammarRules;
        this.numberOfQuestions = numberOfQuestions;
        this.bindingList = bindingList;
        if (bindingList == 0) {
            this.Success_Fail = "Failed";
            this.reason = "binding list is empty";
        }

    }

    public Integer getBindingList() {
        return bindingList;
    }

    public String getSuccess_Fail() {
        return Success_Fail;
    }

    public String getReason() {
        return reason;
    }

    public String getFrameType() {
        return frameType;
    }

    public Integer getNumberOfGrammarRules() {
        return numberOfGrammarRules;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public static void findNumberOfGrammarRules(File file) throws Exception {
        Integer index = 0;

        index = index + 1;
        ObjectMapper mapper = new ObjectMapper();
        GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
        Integer total = grammarEntries.getGrammarEntries().size();
        Integer idIndex = 0, noIndex = 0;
        processEachGrammarUnit(grammarEntries, noIndex, idIndex);

    }

    public static void processEachGrammarUnit(GrammarEntries grammarEntries, Integer noIndex, Integer idIndex) throws IOException, Exception {
        Double nounPPNumber = 0.0, transitiveNumber=0.0, inTransitivePPNumber=0.0, 
                predicateNumber=0.0,superlativeNumber=0.0;
        Double nounPPLex = 0.0, transitiveLex=0.0, inTransitivePPLex=0.0, 
                predicateLex=0.0,superlativeLex=0.0;
        Set<String> nounPPFrames = new HashSet<String>();
        Set<String> transitiveFrames = new HashSet<String>();
        Set<String> inTransitivePPFrames = new HashSet<String>();
        Set<String> adjectivePredicateFrames = new HashSet<String>();
        Set<String> superlativeFrames = new HashSet<String>();

        for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
             String lexEntry="";
            if(grammarEntryUnit.getLexicalEntryUri()!=null){
               lexEntry=grammarEntryUnit.getLexicalEntryUri().toString(); 
            }
            List<String> rules = grammarEntryUnit.getSentences();
            Integer sizeOfGrammar=rules.size();
            if (grammarEntryUnit.getFrameType().contains("NP")) {
                nounPPNumber += sizeOfGrammar;
                nounPPFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("VP")){
               transitiveNumber+=sizeOfGrammar; 
                transitiveFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("IPP")){
               inTransitivePPNumber+=sizeOfGrammar; 
               inTransitivePPFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("AA")){
               predicateNumber+=sizeOfGrammar; 
               adjectivePredicateFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("AG")){
               superlativeNumber+=sizeOfGrammar; 
               superlativeFrames.add(lexEntry);
            }
        }
        nounPPLex =(double) nounPPFrames.size();
        transitiveLex=(double)transitiveFrames.size();
        inTransitivePPLex=(double)inTransitivePPFrames.size(); 
        predicateLex=(double)adjectivePredicateFrames.size();
        superlativeLex=(double)superlativeFrames.size();
        
        Double  nounPPLexGiven = 724.0, transitiveLexGiven=52.0, inTransitivePPLexGiven=57.0, 
                predicateLexGiven=35.0,superlativeLexGiven=22.0;
          
        Double nounPPResult = (nounPPNumber*nounPPLexGiven)/nounPPLex, 
               transitiveResult=(transitiveNumber*transitiveLexGiven)/transitiveLex,
               inTransitivePPResult=(inTransitivePPNumber*inTransitivePPLexGiven)/inTransitivePPLex, 
               predicateResult=(predicateNumber*predicateLexGiven)/predicateLex, 
               superlativeResult=(superlativeNumber*superlativeLexGiven)/superlativeLex;
        
        System.out.println("nounPPLex::"+nounPPLex+" nounPPNumber::"+nounPPNumber+" nounPPLexGiven::"+nounPPLexGiven+" relNounPPFrameNumber::"+nounPPResult);
        System.out.println("transitiveFrames::"+transitiveLex+" transitiveFramesNumber::"+transitiveNumber+" transitiveLexGiven::"+transitiveLexGiven+" relTransitiveFrameNumber::"+transitiveResult);
        System.out.println("inTransitivePPFrames::"+inTransitivePPLex+" inTransitivePPFrameNumber::"+inTransitivePPNumber+" inTransitivePPLexGiven::"+inTransitivePPLexGiven+" relInTransitivePPFrameNumber::"+inTransitivePPResult);
        System.out.println("adjectivePredicateFrames::"+predicateLex+" adjectivePredicateNumber"+predicateNumber+" predicateLexGiven::"+predicateLexGiven+" relAdjectivePredicateNumber::"+predicateResult);
        System.out.println("superlativeFrames::"+superlativeLex+" superlativeFrameNumber::"+superlativeNumber+" superlativeLexGiven::"+superlativeLexGiven+" relSuperlativeFrameNumber::"+superlativeResult);
     
    }

    public static void main(String[] args) throws Exception {
        //countGrammarRules();
        String inputDir = "grammarFiles/en/";
        String fileName = "grammar_FULL_DATASET_EN.json";
        File file = new File(inputDir + fileName);
        //Statistics.findNumberOfGrammarRules(file);
        inputDir = "result/en/";
        fileName = "/ErrorAnalysis.csv";
        file = new File(inputDir + fileName);
        Statistics.errorAnalysis(file);
        System.out.println("hello world!!");

    }

    private static void errorAnalysis(File file) {
        List<String[]> rows = CsvUtils.readAllDataAtOnce(file);
        Map<String, Integer> map = new TreeMap<String, Integer>();
        for (String[] result : rows) {
            Integer value = 1;
            System.out.println(result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[1]);
            String key = result[1];
            if (map.containsKey(key)) {
                value = map.get(key);
                value = value + 1;
            }
            map.put(key, value);
        }
        System.out.println(map + " " + map.size());
        System.out.println(rows.size());
        Double sum = (double) rows.size();
        Double final_result=0.0;
        for (String key : map.keySet()) {
            Double value = (double) map.get(key);
            Double result = (value * 100) / sum;
            System.out.println(key+" "+result);
            final_result+=result;
        }
            System.out.println(final_result);

    }

}

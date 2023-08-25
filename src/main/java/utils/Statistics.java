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
import java.util.LinkedHashSet;
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

    public static void findNumberOfGrammarRules(File file,Double []lexEntries) throws Exception {
        Integer index = 0;

        index = index + 1;
        ObjectMapper mapper = new ObjectMapper();
        GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
        Integer total = grammarEntries.getGrammarEntries().size();
        Integer idIndex = 0, noIndex = 0;
        processEachGrammarUnit(grammarEntries, lexEntries,noIndex, idIndex);

    }

    public static void processEachGrammarUnit(GrammarEntries grammarEntries, Double []lexEntries,Integer noIndex, Integer idIndex) throws IOException, Exception {
        Double nounPPSizeOfGrammar = 0.0, transitiveSizeOfGrammar=0.0, inTransitivePPSizeOfGrammar=0.0, 
                predicateSizeOfGrammar=0.0,superlativeSizeOfGrammar=0.0;
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
                nounPPSizeOfGrammar += sizeOfGrammar;
                nounPPFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("VP")){
               transitiveSizeOfGrammar+=sizeOfGrammar; 
               transitiveFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("IPP")){
               inTransitivePPSizeOfGrammar+=sizeOfGrammar; 
               inTransitivePPFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("AA")){
               predicateSizeOfGrammar+=sizeOfGrammar; 
               adjectivePredicateFrames.add(lexEntry);
            }
            else if(grammarEntryUnit.getFrameType().contains("AG")){
               superlativeSizeOfGrammar+=sizeOfGrammar; 
               superlativeFrames.add(lexEntry);
            }
        }
        nounPPLex =(double) nounPPFrames.size();
        transitiveLex=(double)transitiveFrames.size();
        inTransitivePPLex=(double)inTransitivePPFrames.size(); 
        predicateLex=(double)adjectivePredicateFrames.size();
        superlativeLex=(double)superlativeFrames.size();
        
        Double []lex=new Double[]{nounPPLex,transitiveLex,inTransitivePPLex,predicateLex,superlativeLex}; 
        Double []sizeOfGramma=new Double[]{nounPPSizeOfGrammar,transitiveSizeOfGrammar,inTransitivePPSizeOfGrammar,predicateSizeOfGrammar,superlativeSizeOfGrammar}; 
        calculate(lexEntries,lex,sizeOfGramma);
     
    }
    
    public static Integer[] countNumberOfGramarRules(File file, String frameType) throws IOException, Exception {

        ObjectMapper mapper = new ObjectMapper();
        GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
        Integer total = grammarEntries.getGrammarEntries().size();
        Integer nounPPSizeOfGrammar = 0, nounPPLex = 0;
        Set<String> nounPPFrames = new HashSet<String>();

        for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
            String lexEntry = "";
            if (grammarEntryUnit.getLexicalEntryUri() != null) {
                lexEntry = grammarEntryUnit.getLexicalEntryUri().toString();
            }
            List<String> rules = grammarEntryUnit.getSentences();
            Integer sizeOfGrammar = rules.size();
            if (grammarEntryUnit.getFrameType().contains(frameType)) {
                nounPPSizeOfGrammar += sizeOfGrammar;
                nounPPFrames.add(lexEntry);
            }

        }
        nounPPLex =  nounPPFrames.size();
        //Double nounPPResult = (nounPPSizeOfGrammar * nounPPLexGiven) / nounPPLex;
        return new Integer[]{nounPPLex,nounPPSizeOfGrammar};

    }
    

    private static void calculate(Double[] lexEntries,Double []lex,Double []sizeOfGramma) {
        Double  nounPPLexGiven = lexEntries[0], transitiveLexGiven=lexEntries[1], inTransitivePPLexGiven=lexEntries[2], 
                predicateLexGiven=lexEntries[3],superlativeLexGiven=lexEntries[4];
        Double  nounPPLex = lex[0], transitiveLex=lex[1], inTransitivePPLex=lex[2], 
                predicateLex=lex[3],superlativeLex=lex[4];
        Double  nounPPNumber = sizeOfGramma[0], transitiveNumber=sizeOfGramma[1], inTransitivePPNumber=sizeOfGramma[2], 
                predicateNumber=sizeOfGramma[3],superlativeNumber=sizeOfGramma[4];
      
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
            System.out.println("final_result::"+final_result);

    }
    
     public static void main(String[] args) throws Exception {
        //countGrammarRules();
        //String inputDir = "grammarFiles/en/";
        //String fileName = "grammar_FULL_DATASET_EN.json";
        /*String dir = "/home/elahi/A-Grammar/multilingual-grammar-generator/result/";

        String[] languaes=new String[]{"en","de","it","es"};
        languaes=new String[]{"en"};
        String[] frames=new String[]{"NPP","VP","IPP","AA","AG"};
         for (String languae : languaes) {
              String inputDir =dir+languae+"/";

             for (String frameType : frames) {
                 String fileName = "grammar_FULL_DATASET_" + frameType + "_EN.json";
                 File file = new File(inputDir + fileName);
                 Integer[] result = countNumberOfGramarRules(file, frameType);
                 Double nounPPLexGiven = 0.0;
                 if (frameType.contains("NPP")) {
                     nounPPLexGiven = 724.0;
                 } else if (frameType.contains("VP")) {
                     nounPPLexGiven = 52.0;
                 } else if (frameType.contains("IPP")) {
                     nounPPLexGiven = 57.0;
                 } else if (frameType.contains("AA")) {
                     nounPPLexGiven = 35.0;
                 } else if (frameType.contains("AG")) {
                     nounPPLexGiven = 22.0;
                 }
                 Double numberofLexicalEntry = (double) result[0];
                 Double numberOfGrammarRules = (double) result[1];
                 Double nounPPResult = (numberOfGrammarRules * nounPPLexGiven) / numberofLexicalEntry;
                 System.out.println("numberofLexicalEntry::" + numberofLexicalEntry + " numberOfGrammarRules::" + numberOfGrammarRules
                         + " relNounPPLexGiven::" + nounPPLexGiven + " relNounGrammar::" + nounPPResult);
             }
         }*/


        
        Integer sum=4984+3078+2183+910+50;
        sum=5102+5345+4972+501+70;
        sum=3820+3001+1593+613+34;
        sum=287+81+78+13+11;
        sum=57+85+97+14+9;
        sum=3512+2987+1503+519+27;
        System.out.println("sum::"+sum);
        //Double []englishLexEntries=new Double[]{724.0,52.0,57.0,35.0,22.0};
        //Double []germanLexEntries=new Double[]{416.0,60.0,65.0,15.0,10.0};
        //Statistics.findNumberOfGrammarRules(file,englishLexEntries);
        //Double []lex=new Double[]{101.0,transitiveLex,inTransitivePPLex,predicateLex,superlativeLex}; 
        //Double []sizeOfGramma=new Double[]{nounPPSizeOfGrammar,transitiveSizeOfGrammar,inTransitivePPSizeOfGrammar,predicateSizeOfGrammar,superlativeSizeOfGrammar}; 
        //Statistics.calculate(lex, lex, sizeOfGramma);
        String inputDir = "result/en/";
        String fileName = "/ErrorAnalysis.csv";
        fileName = "ChatGptErrorAnalysis.csv";
        File file = new File(inputDir + fileName);
        Statistics.errorAnalysis(file);
        Integer sumAll=31+5+7+8+8+7+5+10+3+7+4+8;
        System.out.println(sumAll);
        System.out.println("hello world!!");

    }


}

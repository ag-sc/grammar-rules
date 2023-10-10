package evalution;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import utils.FscoreCalculation;
import utils.Result;
import utils.Results;
import utils.SparqlQuery;
import utils.StringModifier;
import utils.csv.CsvUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi evaluate qald questions
 */
public class Evalution {
    private static Boolean online=false;

    private String[] head = new String[]{"ID", "status", "sentence", "sparqlQueGG", "sparqlQald",
        "TP", "FP", "FN", "Precision", "Recall", "Fscore", "Status"};

    public Evalution() {
       

    }
    public void evalute(String inputDir, String qaldDataType, String dataSetType, String inductive,String language) throws IOException {
         File[] files = new File(inputDir).listFiles();
        for (File file : files) {
            if (file.getName().contains("output-") && file.getName().contains(qaldDataType)
                    && file.getName().contains(dataSetType) && file.getName().contains(inductive)) {
                List<String[]> rows = this.getParseResults(file);
                File outputFile = new File(inputDir + file.getName().replace("output-", "evaluation-").replace(".json", ".csv"));
                List<String[]> outputs = this.evalute(rows,language);
                CsvUtils.writeDataAtOnce(outputFile, outputs);
            }

        }
    }

    public List<String[]> evalute(List<String[]> rows,String language) {
        List<String[]> outputs = new ArrayList<String[]>();
        outputs.add(head);

        float globadTp = 0, globalFp = 0, globalFn = 0, globalPrecision = 0, globalRecall = 0, globalFscore = 0, microPreision = 0, microRecall = 0, microFmeasure = 0;
        float macroPreision = 0, macroRecall = 0, macroFmeasure = 0;
        Integer numberOfMatch = 0;
        Integer limit = -1, index = 0;
        for (String[] row : rows) {
            Integer rowIndex = 0;
            String id = null, sparqlQueGG = null, sparqlQald = null, status = null, sentence = null;
            for (String cell : row) {
                System.out.println(row[0] + "  " + row[1] + " " + row[2] + " " + row[3]);
                if (rowIndex == 0) {
                    id = cell;
                } else if (rowIndex == 1) {
                    status = cell;
                } else if (rowIndex == 2) {
                    sentence = cell;
                } else if (rowIndex == 3) {
                    sparqlQueGG = cell;
                } else if (rowIndex == 4) {
                    sparqlQald = cell;
                }
                rowIndex = rowIndex + 1;
            }
            //if (sparqlQueGG != null) {
            //if (!sparqlQueGG.contains("-")) {
            Map<String, String> resultQald = new SparqlQuery(sparqlQald,language,false).getEntityMap();
            Map<String, String> resultQueGG = new SparqlQuery(sparqlQueGG,language,false).getEntityMap();

            System.out.println(id + " queGGSparql=" + sparqlQueGG + " qaldSparql=" + sparqlQald);
            System.out.println(id + " resultQueGG=" + resultQueGG.size() + " resultQald=" + resultQald.size());
            FscoreCalculation cal = new FscoreCalculation(resultQueGG.keySet(), resultQald.keySet());
            //System.out.println(cal.getTp() + " " + cal.getFn() + " " + cal.getFn());
            //System.out.println(cal.getPrecision() + " " + cal.getRecall() + " " + cal.getFscore());
            float tp = cal.getTp();
            float fp = cal.getFp();
            float fn = cal.getFn();
            float precision = cal.getPrecision();
            float recall = cal.getRecall();
            float fscore = cal.getFscore();
            outputs.add(new String[]{id, status, sentence,
                sparqlQueGG, sparqlQald, Float.toString(tp), Float.toString(fp), Float.toString(fn),
                Float.toString(precision), Float.toString(recall), Float.toString(fscore),
                status});
            globadTp += tp;
            globalFp += fp;
            globalFn += fn;
            globalPrecision += precision;
            globalRecall += recall;
            globalFscore += fscore;

            //}
            //}
        }
        try {
            //System.out.println(str);
            FscoreCalculation fscoreTotal = new FscoreCalculation(globadTp, globalFp, globalFn);
            microPreision = fscoreTotal.getPrecision();
            microRecall = fscoreTotal.getRecall();
            microFmeasure = fscoreTotal.getFscore();
            float totalRow = rows.size();
            macroPreision = globalPrecision / totalRow;
            macroRecall = globalRecall / totalRow;
            macroFmeasure = globalFscore / totalRow;
            outputs.add(new String[]{"", "", "", "", "",
                Float.toString(globadTp), Float.toString(globalFp), Float.toString(globalFn),
                Float.toString(microPreision), Float.toString(microRecall), Float.toString(microFmeasure)});
            outputs.add(new String[]{"", "", "", "", "", "", "", "",
                Float.toString(macroPreision), Float.toString(macroRecall), Float.toString(macroFmeasure)});

        } catch (Exception ex) {
            Logger.getLogger(Evalution.class.getName()).log(Level.SEVERE, null, ex);
            ex.getMessage();
        }
        return outputs;
    }

    private List<String[]> getParseResults(File outputFile) throws IOException {
        List<String[]> rows = new ArrayList<String[]>();
        ObjectMapper mapper = new ObjectMapper();
        Results results = mapper.readValue(outputFile, Results.class);
        for (Result result : results.getResults()) {
            System.out.println(result);
            String id = result.getId();
            String status = result.getStatus();
            String sentence = result.getSentence();
            String givenSparql = result.getGivenSparql();
            String queGGSParql = "N";
            if (result.getSparqls().size() > 1) {
                 Map<String,String> propertySparqls = findPropertySparql( result.getSparqls());
                 if(!propertySparqls.isEmpty())
                    queGGSParql =this.match(givenSparql,propertySparqls);
            } else if(result.getSparqls().size() == 1) {
                queGGSParql = result.getSparqls().iterator().next();
            }
            rows.add(new String[]{id, status, sentence, givenSparql, queGGSParql});
        }
        return rows;
    }

    private Map<String,String> findPropertySparql(List<String> sparqls) {
        Map<String,String> propertySparqls=new TreeMap<String,String>();
        for(String sparql:sparqls){
            if(sparql.contains("?Arg")){
              continue;   
            }
            String []info=sparql.split(" ");
            for(String string:info){
                if(string.contains("http:")){
                    if(!string.contains("http://dbpedia.org/resource/")){
                       string=StringModifier.shortUri(string);
                       //string=string.replace("<", "").replace(">","").replace("http://dbpedia.org/ontology/", "dbo:");
                       propertySparqls.put(string, sparql);
                    }
   
                }
            }
        }
        return propertySparqls;
    }

    private String match(String sparql, Map<String, String> propertySparqls) {
        String[] info = sparql.split(" ");
        String property = null;
        for (String string : info) {
             string=StringUtils.substringBetween(sparql, "{", "}");
             String [] elements=string.split(" ");
             Integer index=0;
             for (String element : elements) {
                  element=StringModifier.shortUri(element);
                  if(propertySparqls.containsKey(element)){
                      return propertySparqls.get(element);
                  }
                  index=index+1;
             }
        }
        return propertySparqls.values().iterator().next();

    }
    
   

}

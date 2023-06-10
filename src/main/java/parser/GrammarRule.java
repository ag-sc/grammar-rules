package parser;

import java.io.File;
import utils.QAElement;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import utils.RegularExpression;
import utils.StringModifier;
import utils.SparqlQuery;
import utils.TripleProcess;
import utils.UriLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author  
 */
public class GrammarRule {

    public static Integer RULE_VARIABLE_INDEX = 0;
    public static Integer RULE_REGULAR_EXPRESSION_INDEX = 1;
    public static String ENTITY_DIR = "turtle/";

  
    private QAElement qaElement = null;
    private String bindingType = null;
    private Map<String, String> entityMap = new TreeMap<String, String>();

    // write parse in 
    public GrammarRule(List<String[]> questions, String sparql, String bindingType) {
        this.qaElement = new QAElement(questions, sparql);
        this.bindingType = bindingType;
    }

    public Map<String, String> findEntityMapEndpoint() {
        return new SparqlQuery(this.bindingType).getEntityMap();
    }

    public Map<String, String> findEntityMapFromBindingType(Integer numberOfEntities, String language) throws Exception {
        Map<String, String> entityMap = new TreeMap<String, String>();
        TripleProcess tripleProcess = new TripleProcess();
        String fileName = ENTITY_DIR + File.separator + this.bindingType + ".ttl";
        Set<String> entities = tripleProcess.findSubjObjProp(fileName, numberOfEntities, "subject", language);
        if (entities.isEmpty()) {
            throw new Exception("no entity found for the binding type!!!");
        }
        for (String entity : entities) {
            String label = StringModifier.makeLabel(entity, language);
            entityMap.put(label, entity);
        }
        return entityMap;
    }

    public Boolean parse(String sentence, Boolean entityRetriveOnline, Integer numberOfEntities, String language) throws Exception {
        List<String[]> questions = this.qaElement.getQuestion();
        String sparql = this.qaElement.getSparql();
        if (!questions.isEmpty()) {
            for (String[] rule : questions) {
                String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
                //System.out.println(ruleRegularEx);
                String extractedPart = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
                if (extractedPart!=null) {
                    Map<String, String> entityMap = new TreeMap<String, String>();
                    if (!entityRetriveOnline) {
                        entityMap = this.findEntityMapFromBindingType(numberOfEntities, language);
                    } else {
                        entityMap = this.findEntityMapEndpoint();
                    }
                    //printMap(entityMap);
                    //System.out.println(sentence);
                    //System.out.println(sparql);
                    String result = findUriGivenEntity(extractedPart, entityMap);
                    if (result != null) {
                        if (result.contains("http")) {
                            sparql=prepareSparql(sparql,result);
                            this.qaElement=new QAElement(questions,sparql);
                            return true;
                        } else {
                            result=result.replace("_", " ");
                            this.qaElement=new QAElement(questions,sparql,result);
                            return true;
                        }
                    }
                }
            }
        }
       return false;
    }
    
    String  joinSparql(String mainSparql,String partSparql) {
        String uri=new SparqlQuery(partSparql,true).getSingleResult();
        return prepareSparql(mainSparql, uri);
    }
    
    private String prepareSparql(String sparql, String uri) {
        return sparql.replace("?Arg", "<"+uri+">");
    }

    private String findUriGivenEntity(String extractedPart, Map<String, String> entityMap) {
        String entity = StringModifier.makeLabel(extractedPart, "en");

        if (entityMap.containsKey(entity)) {
            return entityMap.get(entity);
        }
        return entity;
    }

    private String findEntity(String regulardExpr, String sentence) {
        sentence = StringModifier.removeDelimiter(sentence).toLowerCase();
        regulardExpr = regulardExpr.replace("(.*?)", "");
        regulardExpr = StringModifier.removeDelimiter(regulardExpr).toLowerCase();

        sentence = sentence.replace(regulardExpr, "");

        /*List<String> results = StringModifier.findCommonWords(sentence, regulardExpr);
        for (String word : results) {
            sentence = sentence.replace(word, "");
        }*/
        sentence = sentence.stripLeading().stripTrailing().strip().trim();
        return sentence;
    }

    private void printMap(Map<String, String> entityMap) {
        for (String key : entityMap.keySet()) {
            System.out.println(key + " " + entityMap.get(key));
        }
    }

    public QAElement getQaElement() {
        return qaElement;
    }

    public String getSparql() {
        return this.qaElement.getSparql();
    }

    public String getBindingType() {
        return bindingType;
    }

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    @Override
    public String toString() {
        return "GrammarRule{" + "qaElement=" + qaElement + ", entityMap=" + entityMap + '}';
    }

    boolean isParsed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String result() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

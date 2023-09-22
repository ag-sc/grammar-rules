package parser;

import java.io.File;
import utils.QAElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryType;
import static org.apache.jena.riot.web.LangTag.check;
import utils.Dictionary;
import utils.ExtractPart;
import utils.JaccardSimilarity;
import utils.RegularExpression;
import utils.Sorting;
import utils.SparqlQuery;
import utils.StringModifier;
import utils.TripleProcess;
import utils.Tuple;
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
    public static String ENTITY_DIR = "../resources/en/turtle/";
    private QAElement qaElement = null;
    private String template = null;
    public static Map<String, Map<String, List<String>>> regularExpreMap = new TreeMap<String, Map<String, List<String>>>();
    private Dictionary dictionary=null;
    // write parse in 
    
    public GrammarRule(List<String> questions, String sparql, List<String> bindingType,  String returnVariable, String sentenceTemplate,String classFileName) {
        this.template=sentenceTemplate;
        this.dictionary=new Dictionary(classFileName);
        sparql=sparql.replace("WHERE { Answer", "WHERE { ?Answer");
        List<String> bindingSparqls = this.modifySparqlBinding(bindingType,returnVariable, sentenceTemplate, sparql);
        String questionSparql = this.modifyQuestionSparql(sentenceTemplate,returnVariable, sparql);
        this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
        for (String ruleRegularEx : questions) {
            ruleRegularEx = RegularExpression.ruleToRegEx(ruleRegularEx);
            Map<String, List<String>> sparqlMaps = new TreeMap<String, List<String>>();
            if (regularExpreMap.containsKey(ruleRegularEx)) {
                sparqlMaps = regularExpreMap.get(ruleRegularEx);
            }
            sparqlMaps.put(questionSparql, bindingSparqls);
            regularExpreMap.put(ruleRegularEx, sparqlMaps);
        }
    }

  
    public String parse(String sentence, Boolean entityRetriveOnline, Integer numberOfEntities, String language) throws Exception {
        List<String> questions = this.qaElement.getQuestion();
        if (!questions.isEmpty()) {
            if (!isPlaceHolder(this.template)) {
                sentence = sentence.toLowerCase().replace(" ", "_");
                if (regularExpreMap.containsKey(sentence)) {
                    return sentence;
                }
               // else throw new Exception("No place holder but sparql missing!!");
            }

            for (String ruleRegularEx : questions) {
                List<String> results = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
                
                if (!results.isEmpty()) {
                    System.out.println("ruleRegularEx::"+ruleRegularEx);
                    return ruleRegularEx;
                }
            }
        }
        return null;
    }

    public List<String> parse(String sentence, String ruleRegularEx) throws Exception {
        List<String> questions = this.qaElement.getQuestion();
        List<String> questionSparqls = new ArrayList<String>();

        if (!isPlaceHolder(this.template)) {
            Map<String, List<String>> sparqls = regularExpreMap.get(ruleRegularEx);
            for (String questionSparql : sparqls.keySet()) {
                questionSparqls.add(questionSparql);
            }
        }
        List<String> results = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
        if (!results.isEmpty()) {
            //List<String> extractedParts =this.filterMatches(this.qaElement.getBindingSparqls(),results);
            ExtractPart extractPartInfor = new ExtractPart(this.filterMatches(this.qaElement.getBindingSparqls(), results));
            System.out.println(extractPartInfor);

            Map<String, List<String>> sparqls = regularExpreMap.get(ruleRegularEx);
            for (String questionSparql : sparqls.keySet()) {
                List<String> bindingSparqls = sparqls.get(questionSparql);
                for (String bindingSparql : bindingSparqls) {
                    //questionSparql=addRestriction(extractedParts,questionSparql);
                    String sparql = isExceptional(extractPartInfor, bindingSparql);
                    if (sparql != null) {
                        questionSparql = isExceptional(extractPartInfor, bindingSparql);
                        if (extractPartInfor.getRestrictionClassVariable() != null) {
                            questionSparql = addRestriction(extractPartInfor.getRestrictionClassVariable(), questionSparql);
                        }
                        questionSparqls.add(questionSparql);
                    } else {
                        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
                        entityMaps = this.findEntityMapEndpoint(bindingSparql);
                        if (!entityMaps.isEmpty()) {
                            questionSparql = this.findEntity(questions, entityMaps, extractPartInfor.getEntities(), bindingSparqls, questionSparql);
                            if (extractPartInfor.getRestrictionClassVariable() != null) {
                                questionSparql = addRestriction(extractPartInfor.getRestrictionClassVariable(), questionSparql);
                            }
                            questionSparqls.add(questionSparql);
                        }
                    }

                }
            }
        }

        return questionSparqls;
    }

   
    public String findEntity(List<String> questions, List<Map<String, String>> entityMaps,
            List<String> extractedParts, List<String> bindingSparqls,
            String questionSparql) throws Exception {

        LinkedHashSet<String> resultsTemp = findUriGivenEntity(extractedParts, entityMaps);

        try {
            if (!resultsTemp.isEmpty()) {
                if (resultsTemp.size() == 1) {
                    String result = resultsTemp.iterator().next();
                    if (result.contains("http")) {
                        questionSparql = prepareSparql(questionSparql, result);
                        this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
                        return questionSparql;
                    } else {
                        questionSparql = prepareSparql(qaElement.getQuestionSparql(), result);
                        result = result.replace("_", " ");
                        this.qaElement = new QAElement(questions, bindingSparqls, questionSparql, result);
                        return questionSparql;
                    }

                } else if (resultsTemp.size() > 1) {
                    questionSparql = prepareSparql(questionSparql, resultsTemp);
                    this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
                    return questionSparql;
                }

            } else {
                String npPhrase = extractedParts.iterator().next();
                //String newSparql = this.parse(npPhrase, entityRetriveOnline, numberOfEntities, language);
                this.qaElement = new QAElement(questions, bindingSparqls, questionSparql, npPhrase);
                return questionSparql;
            }
        }catch(Exception ex ){
           return "No Sparql!!!!";
        }

        return null;
    }

    public List<Map<String, String>> findEntityMapEndpoint(List<String> bindingSparqls) {
        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
        for (String bindingSparql : bindingSparqls) {
            entityMaps.add(new SparqlQuery(bindingSparql).getEntityMap());
        }
        return entityMaps;
    }
    
    public List<Map<String, String>> findEntityMapEndpoint(String bindingSparql) {
        System.out.println(bindingSparql);
        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
        entityMaps.add(new SparqlQuery(bindingSparql).getEntityMap());
        return entityMaps;
    }

    /*public Map<String, String> findBindingTypeOffline(Integer numberOfEntities, String language) throws Exception {
        Map<String, String> entityMap = new TreeMap<String, String>();
        TripleProcess tripleProcess = new TripleProcess();
        String fileName = ENTITY_DIR + File.separator + this.bindingType + ".ttl";
        Set<String> entities = tripleProcess.findSubjObjPropOffLine(fileName, numberOfEntities, "subject", language);
        if (entities.isEmpty()) {
            throw new Exception("no entity found for the binding type!!!");
        }
        for (String entity : entities) {
            String label = StringModifier.makeLabel(entity, language);
            entityMap.put(label, entity);
        }
        return entityMap;
    }*/

    private String modifyQuestionSparql(String template, String returnVariable, String sparql) {
        if (sparql.contains(QueryType.SELECT.name())) {
            if (template!=null&&template.contains("superlative")) {
                sparql = sparql.replace("?VARIABLE", "?Arg");//.replace("Answer", "subjOfProp"); 
            } else {
                if (returnVariable.contains("objOfProp")) {
                    sparql = sparql.replace("subjOfProp", "Arg");//.replace("Answer", "objOfProp");
                } else {
                    sparql = sparql.replace("objOfProp", "Arg");//.replace("Answer", "subjOfProp");
                }
            }

        }

        return sparql;
    }

    private List<String> modifySparqlBinding(List<String> bindingTypes, String returnVariable, String template, String sparql) {
        List<String> sparqls = new ArrayList<String>();
        String bindingVariable=findBindingVariable(returnVariable);
        if (sparql.contains(QueryType.SELECT.name())) {
            if (template != null && template.contains("superlative")) {
                sparql = "SELECT ?subjOfProp WHERE {  ?subjOfProp <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingTypes.get(0) + "> . } ";
            }
            else if (template != null && template.contains("HOW_MANY_THING_FORWARD")) {
                sparql = sparql.replace("(COUNT(DISTINCT ?Answer) as ?c)", "?"+bindingVariable);
            }
            else  {
                sparql = sparql.replace("SELECT ?Answer", "SELECT ?"+bindingVariable);
            } 
           
            sparqls.add(sparql);

        } else if (sparql.contains(QueryType.ASK.name())) {
            //sparql = sparql.replace("?objOfProp", "?Arg");
            String sparqlArg1 = "SELECT ?Answer WHERE {?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingTypes.get(0) + "> . } ";
            String sparqlArg2 = "SELECT ?Answer WHERE {?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingTypes.get(1) + "> . } ";
            sparqls.add(sparqlArg1);
            sparqls.add(sparqlArg2);
        }
        return sparqls;
    }

    String joinSparql(String mainSparql, String partSparql) throws Exception {
        String uri = new SparqlQuery(partSparql, true).getSingleResult();
        return prepareSparql(mainSparql, uri);
    }

    private String prepareSparql(String sparql, String uri) {
        if(uri.contains("<"))
            return sparql.replace("?Arg",   uri  );
        else 
         return sparql.replace("?Arg",  "<" + uri + ">" );
    }

    private String prepareSparql(String sparql, LinkedHashSet<String> uris) {
        if (uris.size() == 1) {
            String value = uris.iterator().next();
            return sparql.replace("?Arg", "<" + value + ">");
        } else {
            Integer index = 1;
            for (String uri : uris) {
                if (index == 1) {
                    sparql = sparql.replace("subjOfProp", "<" + uri + ">");
                } else if (index == 2) {
                    sparql = sparql.replace("objOfProp", "<" + uri + ">");
                }
                index = index + 1;
            }

        }

        return sparql;
    }
    
    private LinkedHashSet<String> findUriGivenEntity(List<String> extractedParts, List<Map<String, String>> entityMaps) throws Exception {
        LinkedHashSet<String> entities = new LinkedHashSet<String>();
        for (String extractedPart : extractedParts) {
            extractedPart = StringModifier.makeLabel(extractedPart, "en");
            for (Map<String, String> entityMap : entityMaps) {
                JaccardSimilarity jac = new JaccardSimilarity(extractedPart, entityMap);
                Double score=jac.getScore();
                String uri=jac.getBestMatch();
                if(uri!=null)
                 entities.add( uri);
            }

        }

        return entities;
    }

    /*private LinkedHashSet<String> findUriGivenEntity(List<String> extractedParts, List<Map<String, String>> entityMaps) {
        LinkedHashSet<String> entities = new LinkedHashSet<String>();
        for (String extractedPart : extractedParts) {
            String entity = StringModifier.makeLabel(extractedPart, "en");
            for (Map<String, String> entityMap : entityMaps) {
                System.out.println(entityMap.keySet());
                if (entityMap.containsKey(entity)) {
                    String uri = entityMap.get(entity);
                    entities.add(uri);
                }
            }

        }

        return entities;
    }*/


    /*private List<String> findUriGivenEntity(List<String> extractedParts, Map<String, String> entityMap) {
        List<String> entities = new ArrayList<String>();
        for (String extractedPart : extractedParts) {
            String entity = StringModifier.makeLabel(extractedPart, "en");

            if (entityMap.containsKey(entity)) {
                entities.add(entityMap.get(entity));
            }
        }

        return entities;
    }*/
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

    /*private String modifySparqlForResult(String sparql) {
        if (queryType.contains(QueryType.SELECT.name())) {
            if (returnVariable.contains("objOfProp")) {
                sparql = sparql.replace( "?Answer","?subjOfProp");
            } else {
                sparql = sparql.replace( "?Answer","?objOfProp");
            }
        } else if (queryType.contains(QueryType.ASK.name())) {
            sparql = sparql.replace( "?Answer","?objOfProp");
        }

        return sparql;
    }*/
    @Override
    public String toString() {
        return "GrammarRule{" + "qaElement=" + qaElement.getQuestionSparql() + '}';
    }

    private String findBindingVariable(String returnVariable) {
        if (returnVariable.contains("objOfProp")) {
            return "subjOfProp";
        } else {
            return "objOfProp";
        }
    }

    private boolean isParsed(String sparql) {
        if(sparql.contains("?Arg")){
            return false;
        }
        return true;
    }

    /*private String isSparqlMatch(List<String> bindingSparqls, String givenSparql) {
        for (String bindingSparql : bindingSparqls) {
            String bindingProperty = findProperty(bindingSparql);
            String goldProperty = findProperty(givenSparql);
            if (bindingProperty.contains(goldProperty)) {
                return bindingSparql;
            }
        }
        return null;
    }

    private String findProperty(String bindingSparql) {
        bindingSparql = StringUtils.substringBetween(bindingSparql, "{", "}");
        bindingSparql = bindingSparql.replace("dbo:", "http://dbpedia.org/ontology/");
        bindingSparql = bindingSparql.replace("dbp:", "http://dbpedia.org/property/");
        bindingSparql=bindingSparql.strip().stripLeading().stripTrailing().trim();
        String[] info = bindingSparql.split(" ");
        Integer index = 0;
        for (String token : info) {
            if(index==1)
              return token;
           
            index=index+1;
        }
        return null;
    }*/

    private String checkMeasure(String value) {
        Set<String> measures=new HashSet<String>(Arrays.asList(new String[]{"meter","second","mole","ampere","kelvin","candela","kilogram"}));
        for(String measure:measures){
            if(value.contains(measure)){
                if(value.contains("_")){
                    String[]info=value.split("_");
                    return info[0];
                }
            }
        }
      return value;
    
    }

    private boolean isPlaceHolder(String template) {
        if (template != null && (template.contains("superlativeWorld") || template.contains("predicateAdjectiveBaseForm"))) {
            return false;
        }
        return true;
    }

    private String isExceptional(ExtractPart extractPart, String bindingSparql) {

        /*if (extractedParts.size() == 2) {
            String className = extractedParts.get(0);
            className = this.dictionary.getClass(className);
            bindingSparql = bindingSparql.replace("className", className);
            String value =extractedParts.get(1);
            value = checkMeasure(value);
            bindingSparql = bindingSparql.replace("VARIABLE", value);
        }*/
        if (this.template != null && this.template.contains("comperative")) {
            String value = extractPart.getEntities().iterator().next();
            value = checkMeasure(value);
            return  bindingSparql.replace("VARIABLE", value);
        }

        return null;
    }

    /*private List<String> filterMatches(List<String> bindingSparqls, List<String> results) {
        List<String> filterResults = new ArrayList<String>();
        if(bindingSparqls.contains("ASK")){
           return results;  
        }
        else  {
            if (results.size() > 1) {
                filterResults.add(results.get(1));
            } else {
                filterResults.add(results.iterator().next());
            }

        }
        return filterResults;
    }*/
    
    private List<String> filterMatches(List<String> bindingSparqls, List<String> results) {
        return results;
    }
    
    public static void setDictionary(Map<String, String> classDictionary){
        
    }

    private String addRestriction(String string, String questionSparql) {
        questionSparql = this.dictionary.getClassRestriction(string, questionSparql);
        return questionSparql;
    }
    
      /*public String parse(String sentence, Boolean entityRetriveOnline, Integer numberOfEntities, String language) throws Exception {
        List<String> questions = this.qaElement.getQuestion();
        
        if (!questions.isEmpty()) {
            for (String ruleRegularEx : questions) {
                List<String> extractedParts = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
                if (!extractedParts.isEmpty()) {
                    Map<String, List<String>> sparqls = regularExpreMap.get(ruleRegularEx);
                    for (String questionSparql : sparqls.keySet()) {
                        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
                        List<String> bindingSparqls = sparqls.get(questionSparql);
                       
                        if (!entityRetriveOnline) {
                            //entityMap = this.findBindingTypeOffline(numberOfEntities, language);
                        } else {
                            entityMaps = this.findEntityMapEndpoint(bindingSparqls);
                        }

                        LinkedHashSet<String> resultsTemp = findUriGivenEntity(extractedParts, entityMaps);
                        if (!resultsTemp.isEmpty()) {
                            if (resultsTemp.size() == 1) {
                                String result = resultsTemp.iterator().next();
                                if (result.contains("http")) {
                                    questionSparql = prepareSparql(questionSparql, result);
                                    this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
                                    return questionSparql;
                                } else {
                                    questionSparql = prepareSparql(qaElement.getQuestionSparql(), result);
                                    result = result.replace("_", " ");
                                    this.qaElement = new QAElement(questions, bindingSparqls, questionSparql, result);
                                    return questionSparql;
                                }

                            } else if (resultsTemp.size() > 1) {
                                questionSparql = prepareSparql(questionSparql, resultsTemp);
                                this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
                                return questionSparql;
                            }

                        } else {
                            String npPhrase = extractedParts.iterator().next();
                            //String newSparql = this.parse(npPhrase, entityRetriveOnline, numberOfEntities, language);
                            this.qaElement = new QAElement(questions, bindingSparqls, questionSparql, npPhrase);
                            return questionSparql;
                        }
                    }

                }
            }
        }
        return null;
    }*/
    
   
    

}

package parser;

import java.io.File;
import utils.QAElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryType;
import utils.RegularExpression;
import utils.SparqlQuery;
import utils.StringModifier;
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
    public static String ENTITY_DIR = "../resources/en/turtle/";
    private QAElement qaElement = null;
    private String bindingType = null;
    public static Map<String, Map<String, List<String>>> regularExpreMap = new TreeMap<String, Map<String, List<String>>>();

    // write parse in 
    public GrammarRule(List<String[]> questions, String sparql, String bindingType, String returnType, String returnVariable, String queryType, String sentenceTemplate) {
        this.bindingType = bindingType;
        List<String> bindingSparqls = this.modifySparqlBinding(queryType, bindingType, returnType, returnVariable, sentenceTemplate, sparql);
        String questionSparql = this.modifyQuestionSparql(queryType, returnVariable, sparql);
        this.qaElement = new QAElement(questions, bindingSparqls, questionSparql);
        for (String[] rule : questions) {
            String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
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
        List<String[]> questions = this.qaElement.getQuestion();
        //String sparql = this.qaElement.getSparql();
        if (!questions.isEmpty()) {
            for (String[] rule : questions) {
                String ruleRegularEx = rule[GrammarRule.RULE_REGULAR_EXPRESSION_INDEX];
                //System.out.println(ruleRegularEx);
                List<String> extractedParts = RegularExpression.isMatchWithRegEx(sentence, ruleRegularEx);
                if (!extractedParts.isEmpty()) {

                    Map<String, List<String>> sparqls = regularExpreMap.get(ruleRegularEx);
                    for (String questionSparql : sparqls.keySet()) {
                        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
                        List<String> bindingSparqls = sparqls.get(questionSparql);
                        //System.out.println(bindingSparqls);
                        //System.out.println(questionSparql);
                        if (!entityRetriveOnline) {
                            //entityMap = this.findBindingTypeOffline(numberOfEntities, language);
                        } else {
                            entityMaps = this.findEntityMapEndpoint(bindingSparqls);
                        }

                        /*if(extractedParts.contains("mount_everest")){
                        printMap(entityMap);
                        System.out.println(extractedParts);
                    }*/
                        //System.out.println(extractedParts);
                        //System.out.println(entityMaps.size());
                        LinkedHashSet<String> resultsTemp = findUriGivenEntity(extractedParts, entityMaps);
                        //System.out.println(resultsTemp);
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
    }

    public List<Map<String, String>> findEntityMapEndpoint(List<String> bindingSparqls) {
        List<Map<String, String>> entityMaps = new ArrayList<Map<String, String>>();
        for (String bindingSparql : bindingSparqls) {
            entityMaps.add(new SparqlQuery(bindingSparql).getEntityMap());
        }
        return entityMaps;
    }

    public Map<String, String> findBindingTypeOffline(Integer numberOfEntities, String language) throws Exception {
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
    }

    private String modifyQuestionSparql(String queryType, String returnVariable, String sparql) {
        if (queryType.contains(QueryType.SELECT.name())) {
            if (returnVariable.contains("objOfProp")) {
                sparql = sparql.replace("subjOfProp", "Arg").replace("Answer", "objOfProp");
            } else {
                sparql = sparql.replace("objOfProp", "Arg").replace("Answer", "subjOfProp");
            }

        }
        /*else if (queryType.contains(QueryType.ASK.name())) {
            sparql = sparql.replace("objOfProp", "Arg");
        }*/

        return sparql;
    }

    private List<String> modifySparqlBinding(String queryType, String bindingType, String returnType, String returnVariable, String template, String sparql) {
        List<String> sparqls = new ArrayList<String>();
        if (queryType.contains(QueryType.SELECT.name())) {
            if (template != null && template.contains("superlative")) {
                sparql = "SELECT ?Answer WHERE {  ?subjOfProp <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingType + "> . } ";
            }
            if (template != null && template.contains("HOW_MANY_THING_BACKWARD")) {
                sparql = sparql.replace("(COUNT(DISTINCT ?Answer) as ?c)", "?Answer");
            }
            if (returnVariable.contains("objOfProp")) {
                sparql = sparql.replace("?Answer", "?subjOfProp");
            } else {
                sparql = sparql.replace("?Answer", "?objOfProp");
            }
            sparqls.add(sparql);

        } else if (queryType.contains(QueryType.ASK.name())) {
            //sparql = sparql.replace("?objOfProp", "?Arg");
            String sparqlArg1 = "SELECT ?Answer WHERE {?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingType + "> . } ";
            String sparqlArg2 = "SELECT ?Answer WHERE {?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + returnType + "> . } ";
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
        return sparql.replace("?Arg", "<" + uri + ">");
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

    private LinkedHashSet<String> findUriGivenEntity(List<String> extractedParts, List<Map<String, String>> entityMaps) {
        LinkedHashSet<String> entities = new LinkedHashSet<String>();
        for (String extractedPart : extractedParts) {
            String entity = StringModifier.makeLabel(extractedPart, "en");
            for (Map<String, String> entityMap : entityMaps) {
                if (entityMap.containsKey(entity)) {
                    String uri = entityMap.get(entity);
                    entities.add(uri);
                }
            }

        }

        return entities;
    }


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

    public String getBindingType() {
        return bindingType;
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


}

package parser;

import utils.QAElement;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import utils.SparqlQuery;
import utils.UriLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi a grammar rule Each grammar rule is essentially a string with
 * one variable $Arg that is passed by a constructor, in addition to a SPARQL
 * Query where the position of the URI to be inserted is marked with $Arg as
 * well. In the constructor we also pass a list of all entities and their URIs
 * that can fill $Arg. 
 */
public class GrammarRule {

    public static Integer RULE_VARIABLE_INDEX = 0;
    public static Integer RULE_REGULAR_EXPRESSION_INDEX = 1;
    private QAElement qaElement = null;
    private String bindingType = null;
    private Map<String, String> entityMap = new TreeMap<String, String>();

    public GrammarRule(List<String[]> questions, String sparql, String bindingType) {
        this.qaElement = new QAElement(questions, sparql);
        this.bindingType=bindingType;
    }

    public Map<String, String> getEntityMap() {
        return new SparqlQuery(this.bindingType).getEntityMap();
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

    @Override
    public String toString() {
        return "GrammarRule{" + "qaElement=" + qaElement + ", entityMap=" + entityMap + '}';
    }

}

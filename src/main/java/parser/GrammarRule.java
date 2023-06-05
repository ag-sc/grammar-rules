package parser;

import utils.QAElement;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    //  question = "Who is the president of "+argument+" s?";
    //    sparql = "SELECT X X president Sub";

    private QAElement qaElement = null;
    private Map<String, String> entityMap = new TreeMap<String, String>();

    public GrammarRule(List<String> questions, String sparql, List<UriLabel> bindingList) {
       this.qaElement = new QAElement(questions, sparql);
       this.findUriLables(bindingList);
    }
    
    private void findUriLables(List<UriLabel> bindingList) {
        for(UriLabel uriLabel:bindingList){
            entityMap.put(uriLabel.getUri(), uriLabel.getLabel());
        }
    }

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public List<String> getQuestion() {
        return this.qaElement.getQuestion();
    }

    public String getSparql() {
        return this.qaElement.getSparql();
    }

    @Override
    public String toString() {
        return "GrammarRule{" + "qaElement=" + qaElement + ", entityMap=" + entityMap + '}';
    }

   

}

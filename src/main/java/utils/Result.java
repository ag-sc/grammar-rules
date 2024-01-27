/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("id")
    private String id = "ID";
    @JsonProperty("status")
    private String status = "status";
    @JsonProperty("sentence")
    private String sentence = "sentence";
    //@JsonProperty("givenSparql")
    @JsonIgnore
    private String givenSparql = "givenSparql";
    @JsonProperty("sparqls")
    private List<String> sparqls = new ArrayList<String>();

    public Result() {

    }

    public Result(String idT, String statusT, String sentenceT, String givenSparqlT, List<String> sparqlsT) {
        this.id = idT;
        this.status = statusT;
        this.sentence = sentenceT;
        this.givenSparql = givenSparqlT;
        this.sparqls = sparqlsT;

    }

    public Result(String idT, String statusT, String sentenceT, ArrayList<String> sparqlsT) {
         this.id = idT;
        this.status = statusT;
        this.sentence = sentenceT;
        this.sparqls = sparqlsT;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getSentence() {
        return sentence;
    }

    public String getGivenSparql() {
        return givenSparql;
    }

    public List<String> getSparqls() {
        return sparqls;
    }

    @Override
    public String toString() {
        return "Result{" + "id=" + id + ", status=" + status + ", sentence=" + sentence + ", givenSparql=" + givenSparql + ", sparqls=" + sparqls + '}';
    }

}

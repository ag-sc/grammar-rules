/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {

    @JsonProperty("results")
    private List<Result> results = new ArrayList<Result>();
    
     public Results() {
    }

    public Results(List<Result> parseResults) {
        this.results=parseResults;
    }

 
    public List<Result> getResults() {
        return results;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentenceBindings {

    @JsonProperty("bindingVariableName")
    private String bindingVariableName;
    @JsonProperty("bindingList")
    private List<UriLabel> bindingList;

    public String getBindingVariableName() {
        return bindingVariableName;
    }

    public List<UriLabel> getBindingList() {
        return bindingList;
    }
}

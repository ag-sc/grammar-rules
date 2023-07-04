/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.QueryType;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrammarEntryUnit {
    
    public static String baseUri="http://localhost:8080#";

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("lexicalEntryUri")
    private URI lexicalEntryUri;
    @JsonProperty("lanuage")
    private String language;
    @JsonProperty("type")
    private String type;
    @JsonProperty("bindingType")
    private String bindingType;
    @JsonProperty("returnType")
    private String returnType;
    @JsonProperty("frameType")
    private String frameType;
    @JsonProperty("sentenceTemplate")
    private String sentenceTemplate;
    @JsonProperty("sentences")
    private List<String> sentences;
    @JsonProperty("queryType")
    private QueryType queryType;
    @JsonProperty("sparqlQuery")
    private String sparqlQuery;
    @JsonProperty("executable")
    private String executable;
    @JsonProperty("sentenceToSparqlParameterMapping")
    private SentenceToSparql sentenceToSparqlParameterMapping;
    @JsonProperty("returnVariable")
    private String returnVariable;
    @JsonProperty("sentenceBindings")
    private SentenceBindings sentenceBindings;
    @JsonProperty("combination")
    private Boolean combination;

    public GrammarEntryUnit() {
    }

    public Integer getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getType() {
        return type;
    }

    public String getBindingType() {
        return bindingType;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getFrameType() {
        return frameType;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public String getSentenceToSparqlParameterMappingX() {
        return sentenceToSparqlParameterMapping.getX();
    }

    public String getReturnVariable() {
        return returnVariable;
    }

    public List<UriLabel> getBindingList() {
        return sentenceBindings.getBindingList();
    }

    public String getBindingVariableName() {
        return sentenceBindings.getBindingVariableName();
    }

    public Boolean getCombination() {
        return combination;
    }

    public URI getLexicalEntryUri() {
        return lexicalEntryUri;
    }

    public SentenceToSparql getSentenceToSparqlParameterMapping() {
        return sentenceToSparqlParameterMapping;
    }

    public SentenceBindings getSentenceBindings() {
        return sentenceBindings;
    }

    public String getSentenceTemplate() {
        return sentenceTemplate;
    }

    public String getExecutable() {
        return executable;
    }
    public static String getLexicalEntrywithOutUri(String url) {
        return url.replace(baseUri, "");
    }
    
    public static Map<String, List<GrammarEntryUnit>> getLexicalEntries(List<File> protoSimpleQFiles) {
        Integer index = 0;
        Map<String, List<GrammarEntryUnit>> lexicalEntries = new TreeMap<String, List<GrammarEntryUnit>>();

        for (File file : protoSimpleQFiles) {
            System.out.println(file.getName());
            ObjectMapper mapper = new ObjectMapper();
            try {
                GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
                for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
                   
                    String lexicalEntry = null;
                    if (grammarEntryUnit.getLexicalEntryUri()==null) {
                        lexicalEntry = baseUri+"unknown_" + index;
                        index = index + 1;
                    }
                    else
                        lexicalEntry = grammarEntryUnit.getLexicalEntryUri().toString();
                    
                    lexicalEntry =lexicalEntry.toLowerCase();
                    lexicalEntry=GrammarEntryUnit.getLexicalEntrywithOutUri(lexicalEntry);                    
                    List<GrammarEntryUnit> grammarEntryUnits = new ArrayList<GrammarEntryUnit>();

                    if (lexicalEntries.containsKey(lexicalEntry)) {
                        grammarEntryUnits = lexicalEntries.get(lexicalEntry);
                    }
                    grammarEntryUnits.add(grammarEntryUnit);
                    lexicalEntries.put(lexicalEntry, grammarEntryUnits);
                }
            } catch (IOException ex) {
                Logger.getLogger(GrammarEntryUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lexicalEntries;
    }
    
    public static Map<String, List<GrammarEntryUnit>> getLexicalEntries(List<File> protoSimpleQFiles,String givenFrame) {
        Integer index = 0;
        Map<String, List<GrammarEntryUnit>> lexicalEntries = new TreeMap<String, List<GrammarEntryUnit>>();

        for (File file : protoSimpleQFiles) {
            System.out.println(file.getName());
            ObjectMapper mapper = new ObjectMapper();
            try {
                GrammarEntries grammarEntries = mapper.readValue(file, GrammarEntries.class);
                for (GrammarEntryUnit grammarEntryUnit : grammarEntries.getGrammarEntries()) {
                    if(grammarEntryUnit.getFrameType().toString().contains("APP")){
                        continue;
                    }
                    if(!grammarEntryUnit.getFrameType().toString().contains(givenFrame)){
                        continue;
                    }
                        
                   
                    String lexicalEntry = null;
                    if (grammarEntryUnit.getLexicalEntryUri()==null) {
                        lexicalEntry = baseUri+"unknown_" + index;
                        index = index + 1;
                    }
                    else
                        lexicalEntry = grammarEntryUnit.getLexicalEntryUri().toString();
                    
                    lexicalEntry =lexicalEntry.toLowerCase();
                    lexicalEntry=GrammarEntryUnit.getLexicalEntrywithOutUri(lexicalEntry);                    
                    List<GrammarEntryUnit> grammarEntryUnits = new ArrayList<GrammarEntryUnit>();

                    if (lexicalEntries.containsKey(lexicalEntry)) {
                        grammarEntryUnits = lexicalEntries.get(lexicalEntry);
                    }
                    grammarEntryUnits.add(grammarEntryUnit);
                    lexicalEntries.put(lexicalEntry, grammarEntryUnits);
                }
            } catch (IOException ex) {
                Logger.getLogger(GrammarEntryUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lexicalEntries;
    }



}

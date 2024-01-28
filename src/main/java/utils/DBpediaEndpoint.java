/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author elahi
 */
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class DBpediaEndpoint {

    private String sparqlEndpoint = "http://dbpedia.org/sparql";
    private Boolean bindingFlag = false;
    private int initialCapacity = 16_000_000;
    private Map<String, String> entityMap = new HashMap<String, String>(initialCapacity);

    public DBpediaEndpoint(String endpoint, String queryString, Boolean bindingFlag) {
        this.sparqlEndpoint = endpoint;
        this.bindingFlag = bindingFlag;
        this.entityMap=resultFromEndpoint(queryString, bindingFlag);
    }

    public Map<String, String> resultFromEndpoint(String queryString, Boolean bindingFlag) {
        Map<String, String> entityMap = new HashMap<String, String>(initialCapacity);
        Query query = QueryFactory.create(queryString);

        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query)) {
            ResultSet results = qexec.execSelect();
            // Process the results
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                // Get the value of the "label" variable
                RDFNode label = soln.get("label");
                RDFNode arg = soln.get("Arg");
                Literal literal = label.asLiteral();
                Resource resource = arg.asResource();
                //Resource resource = label.asResource();
                String labelString = literal.getString();
                String resourceString=resource.getURI();
                /*if (label.isLiteral()) {
                    Literal literal = label.asLiteral();
                    System.out.println("Label: " + literal.getString());
                    labelString = literal.getString();
                } 
                if (label.isResource()) {
                    Resource resource = label.asResource();
                    System.out.println("resource: " + resource.getURI());
                    resourceString = resource.getURI();
                }*/
                if (bindingFlag) {
                    entityMap.put(labelString, resourceString);
                } else {
                    entityMap.put(resourceString, labelString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entityMap;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }


    public Boolean getBindingFlag() {
        return bindingFlag;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public static void main(String[] args) {
        String queryString = "SELECT ?label WHERE { <http://dbpedia.org/resource/Apple_Inc.> <http://www.w3.org/2000/01/rdf-schema#label> ?label } LIMIT 10";
        //resultFromEndpoint(queryString);
    }

}

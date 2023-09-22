/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.csv.CsvFile;

/**
 *
 * @author elahi
 */
public class SparqlQuery {

    //private static String endpoint = "https://dbpedia.org/sparql";
    //private static String endpoint = "http://localhost:9999/blazegraph/sparql";
    private static String endpoint = "http://localhost:19999/bigdata/sparql";
    //private static String endpoint = "http://localhost:9999/blazegraph/sparql";
    private String objectOfProperty;
    private String sparqlQuery = null;
    private String command = null;
    private Map<String, String> entityMap = new TreeMap<String, String>();

    private String type = null;
    private Boolean online = false;

    public SparqlQuery(String query, Boolean flag) throws java.lang.Exception {
        if (query != null) {
            this.parseResult(this.executeSparqlQuery(query));
        } else {
            throw new Exception("The sparql query not found!!!" + query);
        }
    }

    public SparqlQuery(String sparqlQuery) {
        if (!this.isValid(sparqlQuery)) {
            return;
        }
        System.out.println(sparqlQuery);
        String resultSparql = executeSparqlQuery(sparqlQuery);
        this.parseResult(resultSparql);
    }
    
     public SparqlQuery(String endpoint,String sparqlQuery) {
         SparqlQuery.endpoint=endpoint;
        if (!this.isValid(sparqlQuery)) {
            return;
        }
        //System.out.println(sparqlQuery);
        String resultSparql = executeSparqlQuery(sparqlQuery);
        this.parseResult(resultSparql);
    }

    public String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null;
        Process process = null;
        try {
            resultUnicode = this.stringToUrlUnicode(query);
            this.command = "curl " + endpoint + "?query=" + resultUnicode;
            //System.out.println(this.command);
            process = Runtime.getRuntime().exec(command);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            Integer index = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
                /*index=index+1;
                System.out.println(index);
                if(index>10000000)
                    break;*/
            }
            result = builder.toString();
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public void parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            System.out.println("localEndpoint is NOT runing!!!!!");
            e.printStackTrace();
        }
        return null;
    }

    private void parseResult(DocumentBuilder builder, String xmlStr) {
        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        String objectOfProperty = childList.item(j).getTextContent().trim();
                        String label = StringModifier.makeLabel(objectOfProperty, "en");
                        //System.out.println(objectOfProperty);
                        entityMap.put(label, objectOfProperty);
                    }
                }

            }

        } catch (SAXException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        }

    }

    public String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    public String getObject() {
        return this.objectOfProperty;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public String getSingleResult() {
        String key = entityMap.keySet().iterator().next();
        return this.entityMap.get(key);
    }

    @Override
    public String toString() {
        return "SparqlQuery{" + "objectOfProperty=" + objectOfProperty + ", sparqlQuery=" + sparqlQuery + '}';
    }

    public SparqlQuery() {

    }

    public static void setEndpoint(String endpointT) {
        endpoint = endpointT;
    }

    private boolean isEntity(String entityUrl) {
        if (entityUrl.contains("http:")) {
            return true;
        }
        return false;
    }

    public static String rdfType(String className) throws IOException {
        if (className.contains(":")) {
            className = className.split(":")[1];
        }
        return "SELECT ?binding WHERE { ?binding <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   <http://dbpedia.org/ontology/" + className + ">.}";
    }

    private boolean isValid(String sparqlQuery) {
        if (sparqlQuery != null) {
            return true;
        }
        return false;
    }

    public static void main(String args[]) throws IOException {
        String sparql = null;
        Map<String, String> entityMap = new TreeMap<String, String>();
        //"SELECT ?o WHERE {  ?o <http://dbpedia.org/ontology/presenter> <http://dbpedia.org/resource/David_Attenborough> .}";
        // String sparql="SELECT ?s WHERE { ?subjOfProp ?p ?o .}";
        //<http://dbpedia.org/resource/BBC_Wildlife_Specials>
        sparql = "SELECT ?objOfProp WHERE { ?Answer <http://dbpedia.org/ontology/foundingYear> ?objOfProp .}";
        //PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX res: <http://dbpedia.org/resource/> SELECT DISTINCT ?s ?uri WHERE { ?s dbo:officialLanguage ?uri }
        /*SparqlQuery sparqlQuery=new SparqlQuery(sparql);
        entityMap=sparqlQuery.getEntityMap();
        String objectOfProperty="http://dbpedia.org/resource/New_York_City";
        String label=StringModifier.makeLabel(objectOfProperty, "en");
        
        for(String key:entityMap.keySet()){
            String value=entityMap.get(key);
            System.out.println(key+" "+value); 

        }
        label="1950";
        if(entityMap.containsKey(label)){
            String value=entityMap.get(label);
            System.out.println("value::"+value); 
           
        }*/

 /*sparql="SELECT ?uri WHERE { ?uri <http://dbpedia.org/ontology/publisher> <http://dbpedia.org/resource/GMT_Games> }";
        sparql="SELECT ?subjOfProp WHERE { ?subjOfProp <http://dbpedia.org/ontology/publisher> ?Answer .}";*/
        
        sparql = "SELECT Distinct ?class WHERE { ?binding <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class.}";
        String endpoint="http://localhost:9999/blazegraph/sparql";
        SparqlQuery sparqlQuery = new SparqlQuery(endpoint,sparql);
        entityMap = sparqlQuery.getEntityMap();
        List<String> classes=new ArrayList<String>();
        for (String key : entityMap.keySet()) {
            if (key.contains("http://dbpedia.org/ontology/")) {
                String value = entityMap.get(key);
                //System.out.println( value);
                classes.add(value);
            }

        }
        //FileUtils.listToFiles(classes, "src/main/resources/classList.txt");
        //System.out.println("size::"+ classes.size());
        
        CsvFile csvFile=new CsvFile(new File("src/main/resources/restriction.csv"));
        List<String[]>rows=new ArrayList<String[]>();

        Set<String> sparqls=new TreeSet<String>();
        endpoint="https://dbpedia.org/sparql";
        Integer index=1;
        for (String className : classes) {
            sparql = "SELECT Distinct ?label WHERE { "+"<"+className+">"+" <http://www.w3.org/2000/01/rdf-schema#label> ?label."
                    + " filter langMatches(lang(?label),\"en\")}";
            //System.out.println("sparql::"+sparql);
            sparqlQuery = new SparqlQuery(endpoint,sparql);
            entityMap = sparqlQuery.getEntityMap();
            //System.out.println(entityMap);
            for (String key : entityMap.keySet()) {
                 String value=entityMap.get(key);
                className=className.replace("http://dbpedia.org/ontology/","dbo:");
                className=className.replace("<", "").replace(">","");
                System.out.println(index.toString()+" "+value+" "+value+"s"+" "+className);
                rows.add(new String[]{index.toString(),value,value+"s",className});
                index=index+1;
             }
            
            //sparqls.add(sparql);
            //System.out.println( sparql);

        }
        csvFile.writeToCSV(rows);
        
        
        //System.out.println("number of entities:" + classes.size());
        //System.out.println("classes:" + classes);
        //System.out.println("entityMap::"+entityMap.keySet());
        //String test = "atlanta_falcons";
        //System.out.println(entityMap.get(test));
        //FileUtils.hashMapOrgtoFile(sparqlQuery.getEntityMap(), "/home/elahi/A-Grammar/grammar-rules/resources/entity.txt");

    }
}

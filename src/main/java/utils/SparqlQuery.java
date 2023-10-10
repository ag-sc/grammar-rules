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
import static java.lang.System.exit;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static String language=null;
    private String sparqlQuery = null;
    private String command = null;
    private Set<String> entitySet = new TreeSet<String>();
    private int initialCapacity = 16_000_000;
    private Map<String, String> entityMap = new HashMap<String, String>(initialCapacity);
    private static String rdfs_label="<http://www.w3.org/2000/01/rdf-schema#label>";
    private Boolean bindingFlag=false;


    private String type = null;
    private Boolean online = false;

    public SparqlQuery(String query, Boolean flag) throws java.lang.Exception {
        if (query != null) {
            this.parseResult(this.executeSparqlQuery(query));
        } else {
            throw new Exception("The sparql query not found!!!" + query);
        }
    }
    
    public SparqlQuery(String sparql, String language, Boolean bindingFlag) {
        this.language = language;
        this.bindingFlag=bindingFlag;
        if (this.bindingFlag) {
            sparql = bindingLabelSparql(sparql, language);
        }
        System.out.println(sparql);
        if (!this.isValid(sparql)) {
            return;
        }
        String resultSparql = executeSparqlQuery(sparql);
        this.parseResult(resultSparql);
    }
    
   
    public SparqlQuery(List<String> patterns, String language) {
        this.language = language;
        GrepMethod grepMethod = new GrepMethod(language, patterns);
        this.entityMap = grepMethod.getEntityMap();
    }
    
    /*public SparqlQuery(String endpoint,String sparqlQuery,String language) {
        this.language=language;
        SparqlQuery.endpoint=endpoint;
         sparqlQuery = addLabelSparql(sparqlQuery, language);
        if (!this.isValid(sparqlQuery)) {
            return;
        }
        String resultSparql = executeSparqlQuery(sparqlQuery);
        this.parseResult(resultSparql);
        System.out.println("entityMap::"+this.entityMap.keySet());
        exit(1);
    }*/

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
                index=index+1;
                //System.out.println("line..."+line);
                //if(index>1000000)
                 //   break;
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
                        if (objectOfProperty != null) {
                            Label label = new Label(objectOfProperty);
                            if(this.bindingFlag){
                              this.entityMap.put(label.getLabel(), label.getUri());   
                            }
                            else{
                               this.entityMap.put(label.getUri(), label.getUri());  
                            }
                        }
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
    
  
    private static String bindingLabelSparql(String sparql, String language) {
        if (sparql.contains("?objOfProp")) {
            sparql = sparql.replace("?objOfProp", "?Arg");
        } else if (sparql.contains("?subjOfProp")) {
            sparql = sparql.replace("?subjOfProp", "?Arg");
        }
        sparql = sparql.replace("}", "\n ?Arg" + " " + rdfs_label + " " + "?label . "
                + " FILTER (langMatches( lang(?label), \"" + language + "\" ) )" + "}");
        sparql = sparql.replace("SELECT ?Arg", "SELECT ?label ?Arg");
        return sparql;
    }
    
    private static String answerLabelSparql(String sparql, String language) {
        sparql = sparql.replace("?Arg", "?Answer");
        sparql = sparql.replace("}", ".\n ?Answer" + " " + rdfs_label + " " + "?label . "
                + " FILTER (langMatches( lang(?label), \"" + language + "\" ) )" + "}");
        sparql = sparql.replace("SELECT ?Answer", "SELECT ?label ?Answer");
        return sparql;
    }

    private String modifySparql(String uri,String language) {
        String sparql = "SELECT ?label WHERE { "+"<"+uri+">"+" <http://www.w3.org/2000/01/rdf-schema#label> ?label "
                + " FILTER (langMatches( lang(?label), \""+language+"\" ) )"+"}";
       
        return sparql;
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
    
     public Boolean runCommandLine(String location, String scriptName, String class_url) throws IOException, InterruptedException {
        String appDir=null; Boolean processSuccessFlag=false;
        String command = "perl " + location + scriptName + " " + appDir + " " + class_url;
        Runtime runTime = Runtime.getRuntime();
        //System.out.println("location + scriptName::" + location + scriptName);
        //String[] commands = {"perl", location + scriptName};
        //System.out.println("command::"+command);
        Process process = runTime.exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // Read any errors from the attempted command
        System.out.println("Error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

        if (process.waitFor() == 0) {
            System.err.println("Process terminated ");
            processSuccessFlag = true;
            return true;
        } else {
            return false;
        }

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
        String bindingSparql = "SELECT ?objOfProp WHERE { ?Answer "+"<http://dbpedia.org/ontology/publisher> ?objOfProp .}";
        String answerSparql = "SELECT ?Answer WHERE { ?Answer <http://dbpedia.org/ontology/publisher> <http://dbpedia.org/resource/GMT_Games> }";
        String languageT = "de";    
        System.out.println(bindingSparql);        
        //String endpoint = "https://dbpedia.org/sparql";
        SparqlQuery bindingSparqlQuery = new SparqlQuery(bindingSparql, languageT,true);
        for (String key : bindingSparqlQuery.entityMap.keySet()) {
             String value=bindingSparqlQuery.entityMap.get(key);
             System.out.println("binding:: "+key+" "+value);

        }
        SparqlQuery answerSparqlQuery = new SparqlQuery(answerSparql, languageT,false);
        for (String key : answerSparqlQuery.entityMap.keySet()) {
             String value=answerSparqlQuery.entityMap.get(key);
             System.out.println("Answer:: "+key+" "+value);

        }

        /*for (String uri : sparqlQuery.entitySet) {
            if(!uri.contains("http://dbpedia.org/resource/")){
                continue;
            }
            String labelSparql = sparqlQuery.modifySparql(uri, languageT);
            System.out.println(uri);
            SparqlQuery sparqlQueryLabel = new SparqlQuery( endpoint,labelSparql, languageT);
            Set<String> labels = sparqlQueryLabel.entitySet;
            List<String> modLabels = StringModifier.makeManualLabel(labels);
            for (String modLabel : modLabels) {
                entityMap.put(modLabel, uri);
                System.out.println(sparqlQuery.entitySet.size()+" uri::" + uri + "  modLabel::" + modLabel);
            }
          
            //break;

        }*/
     
    }

        //sparqlQuery.modifySparql(uri,languageT)
        /*Map<String, String> entityMap = new TreeMap<String, String>();
        sparql = "SELECT ?objOfProp WHERE { ?Answer <http://dbpedia.org/ontology/foundingYear> ?objOfProp .}";

        sparql = "SELECT Distinct ?class WHERE { ?binding <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?class.}";
        String endpoint = "http://localhost:9999/blazegraph/sparql";
        SparqlQuery sparqlQuery = new SparqlQuery(endpoint, sparql);
        entityMap = sparqlQuery.getEntityMap();
        List<String> classes = new ArrayList<String>();
        for (String key : entityMap.keySet()) {
            if (key.contains("http://dbpedia.org/ontology/")) {
                String value = entityMap.get(key);
                classes.add(value);
            }

        }

        CsvFile csvFile = new CsvFile(new File("src/main/resources/restriction.csv"));
        List<String[]> rows = new ArrayList<String[]>();

        Set<String> sparqls = new TreeSet<String>();
        endpoint = "https://dbpedia.org/sparql";
        Integer index = 1;
        for (String className : classes) {
            sparql = "SELECT Distinct ?label WHERE { " + "<" + className + ">" + " <http://www.w3.org/2000/01/rdf-schema#label> ?label."
                    + " filter langMatches(lang(?label),\"en\")}";
            //System.out.println("sparql::"+sparql);
            sparqlQuery = new SparqlQuery(endpoint, sparql);
            entityMap = sparqlQuery.g;
            //System.out.println(entityMap);
            for (String key : entityMap.keySet()) {
                String value = entityMap.get(key);
                className = className.replace("http://dbpedia.org/ontology/", "dbo:");
                className = className.replace("<", "").replace(">", "");
                System.out.println(index.toString() + " " + value + " " + value + "s" + " " + className);
                rows.add(new String[]{index.toString(), value, value + "s", className});
                index = index + 1;
            }

        }
        csvFile.writeToCSV(rows);*/

  

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author elahi
 */
public class SparqlQueryT {

    //private static String endpoint = "https://dbpedia.org/sparql";
    private static String endpoint ="http://localhost:9999/blazegraph/#query";
    private Map<String,String> entityMap = new TreeMap<String,String>();
    private String singleResult =null;
    
    public SparqlQueryT(String query, Boolean flag) throws java.lang.Exception {
        if(query!=null)
           this.parseResult(this.executeSparqlQuery(query));
        else
            throw new Exception("The sparql query not found!!!"+query);
    }

    /*public SparqlQuery(String bindingType) {
        String sparql=this.getSparql(bindingType);
        this.parseResult(this.executeSparqlQuery(sparql));
    }*/
    
    public SparqlQueryT(String sparql) {
        this.parseResult(this.executeSparqlQuery(sparql));
    }

    public String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null;
        Process process = null;
        try {
            resultUnicode = this.stringToUrlUnicode(query);
            String command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
        } catch (Exception ex) {
            Logger.getLogger(SparqlQueryT.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in unicode in sparql query!" + ex.getMessage());
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
            Logger.getLogger(SparqlQueryT.class.getName()).log(Level.SEVERE, null, ex);
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
                        String url= childList.item(j).getTextContent().trim();
                        System.out.println(url);
                        //String label= url.replace("http://dbpedia.org/resource/", "");
                        String label= StringModifier.makeLabel(url, "en");
                        this.entityMap.put(label, url);
                    }
                }

            }

        } catch (SAXException ex) {
            Logger.getLogger(SparqlQueryT.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SparqlQueryT.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no result after sparql query!" + ex.getMessage());
        }

    }

    private String getSparql(String bindingType) {
        return "SELECT ?subOfProp WHERE { ?subOfProp <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/" + bindingType + "> .}";
    }
    private String getSparqlLabel(String url) {
        return "SELECT ?subOfProp WHERE { ?subOfProp <http://www.w3.org/2000/01/rdf-schema##label> <"+url+"> .}";
    }

    public String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public String getSingleResult() {
        String key=entityMap.keySet().iterator().next();
        return this.entityMap.get(key);
    }

    public static void main(String args[]) throws IOException, java.lang.Exception {
        String endpoint = "https://dbpedia.org/sparql";
        //String endpoint = "http://localhost:9999/blazegraph/sparql";
        String url = "http://dbpedia.org/resource/Hundred_Years'_War";
        String sparql = "";
        SparqlQueryT sparqlQuery = new SparqlQueryT(sparql, true);
        //SparqlQuery sparqlQuery=new SparqlQuery("Person");
        //System.out.print(sparqlQuery.getEntityMap());
        sparql = "SELECT distinct ?o WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o .}";
        sparqlQuery.executeSparqlQuery(sparql);
        System.out.println(sparqlQuery.getEntityMap());

    }

    private Exception Exception() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

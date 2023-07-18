/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class OffLineParse {

    private static String[] runParserOffLine(String id, String status, String sentence, String sparqlGold) {
        try {
            id = StringModifier.deleteQuote(id);
            sentence = StringModifier.deleteQuote(sentence);
            String sparql = StringModifier.deleteQuote(sparqlGold).replace("\n", "");
            //if (sparql != null) {
            if (status.contains("WORK")) {
                return new String[]{id, status, sentence, sparqlGold, sparql};
            } else {
                return new String[]{id, status, sentence, sparqlGold, "N"};
            }
            //}
        } catch (Exception ex) {
            Logger.getLogger(OffLineParse.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return new String[]{};
    }

    /*private static String deletePrefix(String sparql) {
        //PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX res: <http://dbpedia.org/resource/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
        sparql=sparql.replace("PREFIX dbo: <http://dbpedia.org/ontology/>", "");
        sparql=sparql.replace("PREFIX res: <http://dbpedia.org/resource/>", "");
        sparql=sparql.replace(" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ","");
        sparql=sparql.trim().strip().stripLeading().stripTrailing();
        System.out.println(sparql);
        return sparql;
    }*/
}

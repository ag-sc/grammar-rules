/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.csv.CsvFile;

/**
 *
 * @author elahi
 */
public class Dictionary {

    private static Map<String, String> classDictionary = new TreeMap<String, String>(Collections.reverseOrder());

    public Dictionary(String classFileName) {
        try {
            getClassDictionary(classFileName);
        } catch (Exception ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getClassDictionary(String classFileName) throws Exception {
        CsvFile csvFile = new CsvFile();
        List<String[]> rows = csvFile.getRows(new File(classFileName));
        for (String[] row : rows) {
            String className = null;
            String wordSingular = row[1].strip().stripLeading().stripTrailing().trim();
            String wordPlural = row[2].strip().stripLeading().stripTrailing().trim();
            wordSingular = wordSingular.toLowerCase().replace(" ", "_");
            wordPlural = wordPlural.toLowerCase().replace(" ", "_");
            className = row[3];
            classDictionary.put(wordSingular, className);
            classDictionary.put(wordPlural, className);
            //System.out.println(wordSingular+" "+wordPlural);
        }
    }

    public String getClassRestriction(String key, String sparql) {
        if (classDictionary.containsKey(key)) {
            String className = classDictionary.get(key);
            if (className.contains(":")) {
                className = className.split(":")[1];
            }
            return sparql.replace("{", "{ ?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   " + "<http://dbpedia.org/ontology/" + className + ">" + ".");
        }
        return sparql;
    }
    
    public static void main(String []args){
        String classFileName = "src/main/resources/LexicalEntryForClass.csv";
        Dictionary dictionary=new Dictionary(classFileName);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class TripleProcess {

    public Set<String> findSubjObjProp(String fileName, Integer numberOfTriples, String type, String language) {
        Set<String> results = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        File file = new File(fileName);
        Integer lineNumber = 0;

        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                lineNumber = lineNumber + 1;
                String subject = null;
                String object = null, property = null;
                if (line != null) {
                    line = line.replace("<", "\n" + "<");
                    line = line.replace(">", ">" + "\n");
                    line = line.replace("\"", "\n" + "\"");
                    String[] lines = line.split(System.getProperty("line.separator"));

                    Integer index = 0;
                    for (String value : lines) {
                        index = index + 1;
                        if (index == 2) {
                            subject = Match.clean(value, language);
                        } else if (index == 6) {
                            object = Match.clean(value, language);
                        } else if (index == 4) {
                            property = Match.clean(value, language);
                        }
                    }

                    if (lineNumber == -1)
                         ; else if (lineNumber == numberOfTriples) {
                        break;
                    }

                    //System.out.println(lineNumber+" subject:" + subject + " " + "object:" + object + " " + "property:" + property);
                    if (subject != null && object != null) {
                        ;
                    } else {
                        continue;
                    }

                    if (subject.contains("__") || object.contains("__")) {
                        continue;
                    }

                    if (type.contains("subject")) {
                        results.add(subject);
                    } else if (type.contains("object")) {
                        if (object.contains("http://dbpedia.org/ontology/")) {
                            results.add(object);
                            //System.out.println("total::5150434"+" now::"+lineNumber + " object:" + object);
                        }
                    } else if (type.contains("property")) {
                        results.add(property);

                    }

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;

    }

  

    public void stringToFile(String content, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();

    }

    public static void main(String[] args) throws IOException {
        TripleProcess tripleProcess = new TripleProcess();
        String instanceTypeFile = "turtle/instance_types_sorted_en.ttl";
        String grepFile = "turtle/class.sh";
        Set<String> temp = tripleProcess.findSubjObjProp(instanceTypeFile,-1, "object", "en");
        String content = "";
        for (String line : temp) {
            String className = line.replace("http://dbpedia.org/ontology/", "");
            line= "grep -w 'http://dbpedia.org/ontology/" + className + "' "+"*.ttl"+">>" + className + ".ttl" + "\n";
            content +=line;
        }
        System.out.println(content);
        tripleProcess.stringToFile(content, grepFile);
    }

}

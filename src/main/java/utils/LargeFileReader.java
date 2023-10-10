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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LargeFileReader {
    private static String output_prefix="output_";
    private static Integer deNum = 3768702, enNUm = 16428925, itNum = 2878181, esNum = 2852773;

    
    public static void main (String []args) {
        String inputDir = "/media/elahi/Elements/A-Projects/multilingualLabels/";
        String outputDir = "labels/";
        LinkedHashMap<String, String> filePath = new LinkedHashMap<String, String>();
        filePath.put("de", "labels_lang=de_uris=en.ttl");
        filePath.put("es", "labels_lang=es_uris=en.ttl");
        filePath.put("it", "labels_lang=it_uris=en.ttl");
        filePath.put("en", "labels_lang=en.ttl"); 
       
        
        //loadLabels(inputDir,outputDir,filePath);
         //readMap(outputDir,filePath);
         execute(outputDir,"gmt");
    }
    
     public static String execute(String outputDir,String query) {
        String result = null, resultUnicode = null;
        Process process = null;
        try {
            String command = "grep \"gmt\" labels/output_labels_lang=de_uris=en.txt";
            System.out.println(command);
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
            System.out.println(result);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQueryT.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        return result;
    }

    
    public static void readMap(String outputDir, LinkedHashMap<String, String> filePath) {
        int initialCapacity = 16_000_000; // Adjust based on your data size
         File file=new File(outputDir);
         File [] files=file.listFiles();

        for (File fileName : files) {
            Map<String, String> entity = new HashMap<String, String>(initialCapacity);
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                
                if(!fileName.getName().contains("=de_")){
                    continue;
                }
                String line;
                Integer number = 0;
                Integer index = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        String label = info[0];
                        String uri = info[1];
                        System.out.println(index+" "+fileName+" label=" + label + " uri=" + uri);
                        index = index + 1;
                        entity.put(label, uri);
                    }
                    /*if (index > 101) {
                        break;
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("hashMap::"+entity.size());
        }
        

    }

    public static void loadLabels (String inputDir,String outputDir,LinkedHashMap<String, String> filePath) {
       

        for (String language : filePath.keySet()) {
            String fileName = filePath.get(language);
            String inputFile = inputDir + fileName;
            String outputFile = outputDir + fileName.replace(".ttl", ".txt").replace("labels_lang", "output_" + "labels_lang");
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                FileWriter fileWriter = new FileWriter(outputFile, true);

                String line;
                Integer number = 0;
                Integer index = 0;
                if (language.contains("de")) {
                    number = deNum;
                } else if (language.contains("en")) {
                    number = enNUm;
                } else if (language.contains("it")) {
                    number = itNum;
                } else if (language.contains("es")) {
                    number = esNum;
                }
                while ((line = reader.readLine()) != null) {
                    String modifyLine = line.replace(">", ">\n");
                    String[] lines = modifyLine.split("\n");
                    String subject = lines[0];
                    String object = lines[2];
                    if (line.contains("@")) {
                        String uri = StringModifier.makeURI(subject);
                        String label = StringModifier.makeLabel(object);
                        System.out.println(language+" now=" + index + " total=" + number.toString() + " label=" + label + " uri=" + uri);
                        index = index + 1;
                        String writeLine = label + "=" + uri + "\n";
                        fileWriter.append(writeLine);
                    }
                    /*if (index > 101) {
                        break;
                    }*/
                }
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}

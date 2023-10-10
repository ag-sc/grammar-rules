/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
public class GrepMethod {

    private String de_filePath = "labels/output_labels_lang=de_uris=en.txt";
    private String en_filePath = "labels/output_labels_lang=de_uris=en.txt";
    private String es_filePath = "labels/output_labels_lang=de_uris=en.txt";
    private String it_filePath = "labels/output_labels_lang=de_uris=en.txt";

    private static LinkedHashMap<String, String> entityMap = new LinkedHashMap<String, String>();

    public GrepMethod(String language, List<String> patterns) {
        String filePath = null;
        if (language.contains("en")) {
            filePath = this.en_filePath;
        } else if (language.contains("de")) {
            filePath = this.de_filePath;
        } else if (language.contains("it")) {
            filePath = this.it_filePath;
        } else if (language.contains("es")) {
            filePath = this.es_filePath;
        }
        this.findResultByGrepMethod(patterns, filePath);

    }

    public void findResultByGrepMethod(List<String> patterns, String filePath) {
        for (String pattern : patterns) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("grep", pattern, filePath);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        String label = info[0];
                        String uri = info[1];
                        entityMap.put(label, uri);

                    }
                    System.out.println(line);
                }
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Grep command executed successfully.");
                } else {
                    System.err.println("Grep command returned an error code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public LinkedHashMap<String, String> getEntityMap() {
        return entityMap;
    }

    public static void main(String[] args) {
        String language = "de";
        String pattern = "gmt";
        List<String> patterns=new ArrayList<String>();
        patterns.add("gmt");
        GrepMethod grepMethod = new GrepMethod(language, patterns);

    }

}

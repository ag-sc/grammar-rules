/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import static java.lang.System.exit;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author elahi
 */
public class FileUtils {

    public static void stringToFile(String content, File fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();

    }

    public static void stringToFile(String content, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();

    }

    public static void mapToFile(Map<String, Integer> map, String fileName) throws IOException {
        String str = "";
        for (String key : map.keySet()) {
            String value = map.get(key).toString();
            String line = key + "=" + value + "\n";
            str += line;
        }
        stringToFile(str, fileName);
    }
    
    public static void ResultToFile(Map<String, String[]> overallResult, String fileName,String[] header) throws IOException {
        List<String[]> csvData = new ArrayList<String[]>();
        CsvFile CsvFile = new CsvFile(new File(fileName));
        csvData.add(header);
        for (String key : overallResult.keySet()) {
            String[] value = overallResult.get(key);
            csvData.add(value);

        }
        CsvFile.writeToCSV(csvData);
    }
    
     public static void ResultToFileRanked(Map<Float, String[]> overallResult, String fileName,String[] header) throws IOException {
        List<String[]> csvData = new ArrayList<String[]>();
        CsvFile CsvFile = new CsvFile(new File(fileName));
        csvData.add(header);
        for (Float key : overallResult.keySet()) {
            String[] value = overallResult.get(key);
            csvData.add(value);

        }
        CsvFile.writeToCSV(csvData);
    }

    public static String fileToString(String fileName) {
        InputStream is;
        String fileAsString = null;
        try {
            is = new FileInputStream(fileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            fileAsString = sb.toString();
        } catch (Exception ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fileAsString;
    }

    public static Set<Integer> fileToSet(String fileName) {
        InputStream is;
        Set<Integer> ids = new TreeSet<Integer>();
        try {
            is = new FileInputStream(fileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                line = buf.readLine();
                Integer id = Integer.parseInt(line);
                ids.add(id);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }

    public static List<File> getFiles(String fileDir, String category, String extension) {
        String[] files = new File(fileDir).list();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(extension)) {
                selectedFiles.add(new File(fileDir + fileName));
            }

        }

        return selectedFiles;

    }

    public static Map<String, String> fileToHashMapEqual(File classFile, String seperator, Integer key) {
        Map<String, String> map = new TreeMap<String, String>();
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(classFile));
            //line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line != null) {
                    line = line.strip().trim();
                    if (line.contains(seperator)) {
                        String uri = line.split(seperator)[0];
                        String label = line.split(seperator)[1];
                        if (key == 1) {
                            map.put(uri, label);
                        } else {
                            map.put(label, uri);
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void hashMaptoFile(Map<String, Set<String>> duplicateUris, String fileName) throws IOException {
        String str = "";
        for (String key : duplicateUris.keySet()) {
            String line = key;
            for (String value : duplicateUris.get(key)) {
                line += value + " ";

            }
            str += line + "\n";
        }
        FileUtils.stringToFile(str, fileName);
    }
    
    public static void hashMapOrgtoFile(Map<String, String> duplicateUris, String fileName) throws IOException {
        String str = "";
        for (String key : duplicateUris.keySet()) {
            String value =duplicateUris.get(key);
            String line=key+"="+value;
            str += line.replace("\"", "") + "\n";
        }
        FileUtils.stringToFile(str, fileName);
    }

    public static Map<String, String[]> getDataFromFile(File file, Integer keyIndex, Integer classIndex, String className) throws IOException, FileNotFoundException, CsvException {
        Map<String, String[]> map = new TreeMap<String, String[]>();
        String fileType = file.getName();
        if (fileType.contains(".csv")) {
            CsvFile csvFile = new CsvFile(file);
            return csvFile.generateBindingMapL(keyIndex, classIndex, className);
        }
        return new TreeMap<String, String[]>();
    }


    public static void listToFiles(List<String> list, String fileName) {
        String str = "";
        for (String element : list) {
            String line = element + "\n";
            str += line;

        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void appendToFile(String fileName, String line) throws IOException {
        File file = new File(fileName);
        boolean b;
        if (file.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.append(System.getProperty("line.separator"));
                bw.append(line);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stringToFile(line, fileName);
        }
    }

    public static void main(String[] args) {
        String fileName = "../resources/en/property/propertyInstane.txt";
        File classFile = new File(fileName);
        Map<String, String> map = new TreeMap<String, String>();
        map = FileUtils.fileToHashMapEqual(classFile, " ", 2);

        for (String key : map.keySet()) {
            String value = map.get(key);
        }

        //String dir = "../resources/en/questions/";
        //findNumberOfQuestion(dir);
        //String outputDir = "output/en/questions/";
        //seperateLexialEntry(outputDir,"numberOfQuestions.csv");
    }

    private static String filterLexEntry(String key) {
        String[] info = key.split("_");
        return info[0].replace("\"", "");
        //replace("http://localhost:8080#", "");
    }

    private static String filterLexEntry2(String key) {
        key = key.replace("0", "");
        key = key.replace("1", "");
        key = key.replace("2", "");
        key = key.replace("3", "");
        key = key.replace("4", "");
        key = key.replace("5", "");
        key = key.replace("6", "");
        key = key.replace("7", "");
        key = key.replace("8", "");
        key = key.replace("9", "");
        key = key.replace("__", "_");

        if (key.contains("_")) {
            key = key.substring(0, key.length());
        }
        return key;
    }

    private static void findNumberOfQuestion(String dir) {
        File[] files = new File(dir).listFiles();
        Map<String, Integer> numOfQ = new TreeMap<String, Integer>();
        Integer index = 0, totalLines = 0, fileNumber = 0;
        for (File file : files) {
            CsvFile CsvFile = new CsvFile(file);
            List<String[]> rows = new ArrayList<String[]>();
            try {

                rows = CsvFile.getRows(file);
            } catch (Exception ex) {
                continue;
            }
            index = 0;
            totalLines = rows.size();
            fileNumber = fileNumber + 1;
            for (String[] row : rows) {
                index = index + 1;
                String key = filterLexEntry(row[0]);
                if (numOfQ.containsKey(key)) {
                    Integer number = numOfQ.get(key) + 1;
                    numOfQ.put(key, number);
                } else {
                    numOfQ.put(key, 1);
                }
            }

        }
        String fileName = dir + "lex_numQ.txt";
        try {
            FileUtils.mapToFile(numOfQ, fileName);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    

    public static List<File> getSpecificFiles(String fileDir, String extension) {
        List<File> selectedFiles = new ArrayList<File>();
        try {
            String[] files = new File(fileDir).list();
            for (String fileName : files) {
                if (fileName.contains(extension)) {
                    selectedFiles.add(new File(fileDir + fileName));
                }
            }

        } catch (Exception exp) {
            System.err.println("file not found!!");
            return new ArrayList<File>();
        }

        return selectedFiles;
    }

    public static void stringToFiles(String str, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String findParameterLexEntries(List<File> selectedFiles) throws IOException, FileNotFoundException, CsvException {
        CsvFile csvFile = new CsvFile();
        String str = "";
        for (File parameterFile : selectedFiles) {
            List<String[]> rows = csvFile.getRows(parameterFile);
            for (String[] row : rows) {
                String lexEntry = row[0].replace("\"", "").strip().stripLeading().stripTrailing().trim();
                String line = lexEntry + "=" + parameterFile.getName() + "\n";
                System.out.println(line);
                str += line;
            }
        }
        return str;
    }

    public static LinkedHashMap<String, String> fileToLinkedHashMap(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader;
        String line = "";
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                if (line != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        map.put(info[0], info[1]);
                    }

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Set<String> FileToSet(String fileName, String parameter) {
        BufferedReader reader;
        String line = "";
        Set<String> set = new TreeSet<String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                if (line != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        if (info[0].contains(parameter)) {
                            set.add(info[0]);
                        }
                    }

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    public static Set<String> FileToSet(String fileName) {
        BufferedReader reader;
        String line = "";
        Set<String> set = new TreeSet<String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                if (line != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        String parameter = info[0];
                        set.add(parameter);
                    }

                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }
    public static List<String> FileToSetOrginal(String fileName) {
        BufferedReader reader;
        String line = "";
        List<String> set = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                line=line.trim().strip().stripLeading().stripTrailing();
                set.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }
    
    public static List<String> FileToList(String fileName) {
        BufferedReader reader;
        String line = "";
        List<String> set = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                if(line.contains("=")){
                    String []info=line.split("=");
                    String value=info[1].replace(">)", ">").replace(" ", "").strip().stripLeading().stripTrailing().trim();
                    value=value.replace(">)", ">");
                    set.add(value);
                }
               
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

   

}

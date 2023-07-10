/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class CsvUtils {

    public static void readDataLineByLine(File file) {

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readAllDataAtOnce(File file) {
        try {
            // Create an object of file reader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            return csvReader.readAll();

            // print Data
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String[]>();
    }
    
    public static void writeDataLineByLine(File file, List<String[]> data) {
        // first create file object for file placed at location
        // specified by filepath
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            for (String[] row : data) {
                writer.writeNext(row);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
  
    public static void writeDataAtOnce(File file, List<String[]> data) {

        FileWriter outputfile;
        try {
            outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CsvUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        String inputDir = "resources/";
        File[] files = new File(inputDir).listFiles();
        for (File file : files) {
            if (file.getName().contains("input-") && file.getName().contains("QALD9")) {
                //readDataLineByLine(file);
                List<String[]> rows = readAllDataAtOnce(file);
                for (String[] row : rows) {
                    for (String cell : row) {
                        System.out.print(cell + "\t");
                    }
                    System.out.println();
                }
            }
        }

    }

}

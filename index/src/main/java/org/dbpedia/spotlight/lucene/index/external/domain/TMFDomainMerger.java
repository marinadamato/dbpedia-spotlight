package org.dbpedia.spotlight.lucene.index.external.domain;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class merges all domain entities retrieved with SPARQL queries and other services
 */

public class TMFDomainMerger {

    public void mergeDir(String inputDirectory, String outputFilePath) throws IOException {
        BufferedWriter writer;
        Path dst = Paths.get(outputFilePath);
        writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);
        File dir = new File(inputDirectory);
        List domainFiles = getAllFiles(dir, inputDirectory);

        Iterator<String> iterator = domainFiles.iterator();
        while (iterator.hasNext()) {
            String domainFilePath = iterator.next();
            BufferedReader reader = new BufferedReader(new FileReader(domainFilePath));
            String uri = reader.readLine();
            while (uri != null) {
                writer.write(uri);
                writer.newLine();
                uri = reader.readLine();
            }
            reader.close();
        }
        writer.close();
        stripDuplicatesFromFile(outputFilePath);
    }

    public void mergeFiles(List inputFiles, String outputFilePath) throws IOException {
        // Save the new file in the old one
        BufferedReader originalFileReader = new BufferedReader(new FileReader(outputFilePath));
        List originalFileRows = new ArrayList();
        String line;
        while (true){
            line = originalFileReader.readLine();
            if(line == null)
                break;
            originalFileRows.add(line);
        }
        originalFileReader.close();
        BufferedWriter writer;
        Path dst = Paths.get(outputFilePath);
        writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);
        Iterator<String> iterator = inputFiles.iterator();
        while (iterator.hasNext()){
            String filePath = iterator.next();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String uri = reader.readLine();
            while (uri != null) {
                writer.write(uri);
                writer.newLine();
                uri = reader.readLine();
            }
            reader.close();
        }
        Iterator<String> iteratorOriginalFile = originalFileRows.iterator();
        while (iteratorOriginalFile.hasNext()){
            writer.write(iteratorOriginalFile.next());
            writer.newLine();
        }
        writer.close();
        stripDuplicatesFromFile(outputFilePath);
    }

    public void stripDuplicatesFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Set<String> lines = new HashSet<String>(100000);
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String unique : lines) {
            writer.write(unique);
            writer.newLine();
        }
        writer.close();
    }

    private List getAllFiles(File folder, String path) {
        List listOfFiles = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getAllFiles(fileEntry, path);
            } else {
                listOfFiles.add(path + "/" + fileEntry.getName());
            }
        }
        return listOfFiles;
    }
}

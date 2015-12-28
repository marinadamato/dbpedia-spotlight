package org.dbpedia.spotlight.lucene.index.external.domain;

import com.hp.hpl.jena.sparql.pfunction.library.str;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class merge all domain entities retrieved with SPARQL queries and other services
 */

public class TMFDomainMerger {

    public void merge(String inputDirectory, String outputFilePath) throws IOException {
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

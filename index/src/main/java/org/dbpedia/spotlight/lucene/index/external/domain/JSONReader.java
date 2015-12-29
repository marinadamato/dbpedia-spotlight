package org.dbpedia.spotlight.lucene.index.external.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is a simple file reader
 */
public class JSONReader {

    private String jsonPath;

    public JSONReader(String path) {
        jsonPath = path;
    }

    public String readJSON() throws IOException {
        String data = "";
        BufferedReader br = new BufferedReader(new FileReader(jsonPath));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        data = sb.toString();
        return data;
    }
}

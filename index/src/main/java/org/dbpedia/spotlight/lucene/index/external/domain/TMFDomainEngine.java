package org.dbpedia.spotlight.lucene.index.external.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * TODO --> Description of the class
 */
public class TMFDomainEngine {

    public static void main(String[] args) throws IOException {

        // TODO Should become a command line parameter
        // TODO Should consider also language parameter for building different indexes
        // TODO Add parameters to pilot the index domain creation. Put them in the config file

        JSONReader jsonReader = new JSONReader("conf/external/domain.json");
        BufferedWriter writer;
        Path dst = Paths.get("data/tellmefirst/dbpedia/en/output/domainURIs.list");
        writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);
        String json = jsonReader.readJSON();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);

        for(JsonNode node : root) {
            TMFDomainQuery tmfDomainQuery = new TMFDomainQuery(getValue("query", node), getValue("endpoint", node));
            int offset = 0;
            Boolean appended = true;
            while (appended) {
                appended = appendEntities(writer, tmfDomainQuery.getEntities(offset));
                offset += 10000; // This is the number the default number of rows returned by online endpoints
            }
        }
        writer.close();
    }

    private static Boolean appendEntities(BufferedWriter writer, List entitiesList) throws IOException {
        Boolean appended = false;
        Iterator<String> iterator = entitiesList.iterator();
        while (iterator.hasNext()) {
            // writer.write(URLDecoder.decode(line, "UTF-8")); Check if you have to decode it
            appended = true;
            writer.write(iterator.next());
            writer.newLine();
        }
        return appended;
    }

    private static String getValue (String string, JsonNode record) {
        return record.get(string) != null ? record.get(string).asText() : "";
    }
}

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

        JSONReader jsonReader = new JSONReader("conf/external/domain.json");
        BufferedWriter writer;
        Path dst = Paths.get("data/tellmefirst/dbpedia/en/output/domainURIs.list");
        writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);
        String json = jsonReader.readJSON();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);

        for(JsonNode node : root) {
            TMFDomainQuery tmfDomainQuery = new TMFDomainQuery(getValue("query", node), getValue("endpoint", node));
            appendEntity(writer, tmfDomainQuery.getEntities());
        }
        writer.close();
    }

    private static void appendEntity(BufferedWriter writer, List entitiesList) throws IOException {
        Iterator<String> iterator = entitiesList.iterator();
        while (iterator.hasNext()) {
            // writer.write(URLDecoder.decode(line, "UTF-8")); Check if you have to decode it
            writer.write(iterator.next());
            writer.newLine();
        }
    }

    private static String getValue (String string, JsonNode record) {
        return record.get(string) != null ? record.get(string).asText() : "";
    }
}

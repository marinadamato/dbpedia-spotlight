package org.dbpedia.spotlight.lucene.index.external.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.dbpedia.spotlight.LDRClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * TODO --> Description of the class
 *
 */
public class TMFDomainEngine {

    final static Log LOG = LogFactory.getLog(TMFDomainEngine.class);

    public static void main(String[] args) throws Exception {

        LOG.info("Start to get domain entities...");

        // TODO Maybe you need to simplify this tool

        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        String domainConfPath = config.getProperty("tellmefirst.domain.conf", "").trim();
        String domainLoggerPath = config.getProperty("tellmefirst.domain.logger", "").trim();
        String domainEntitiesPath = config.getProperty("org.dbpedia.spotlight.data.conceptURIs", "").trim();
        String ldrEndpoint = config.getProperty("tellmefirst.domain.LDRService").trim();

        JSONReader jsonReader = new JSONReader(domainConfPath);
        String json = jsonReader.readJSON();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);

        BufferedWriter writer;
        Path dst = Paths.get(domainEntitiesPath);
        writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);

        BufferedWriter logger;
        Path dstLogger = Paths.get(domainLoggerPath);
        logger = Files.newBufferedWriter(dstLogger, StandardCharsets.UTF_8);

        for(JsonNode node : root) {

            logger.write("Query description: " + getValue("description", node));
            LOG.info("Query description: " + getValue("description", node));
            logger.newLine();

            logger.write("Language: " + getValue("language", node));
            LOG.info("Language: " + getValue("language", node));
            logger.newLine();

            TMFDomainQuery tmfDomainQuery = new TMFDomainQuery(getValue("query", node), getValue("endpoint", node));
            int offset = 0;
            int appended = 0;
            int totalAppended = 0;

            appended = appendEntities(writer, tmfDomainQuery.getEntities(offset), getValue("baseuri", node)); // Do it at least once

            while (appended != 0) {
                // TODO Add try/catch
                appended = appendEntities(writer, tmfDomainQuery.getEntities(offset), getValue("baseuri", node));
                totalAppended += appended;
                offset += 10000; // This is the default number of rows returned by online endpoints
            }

            logger.write("Number of entities retrieved with query: " + totalAppended);
            LOG.info("Number of entities retrieved with query: " + totalAppended + "\n\n");
            logger.newLine();

            logger.write("Removing duplicates");
            LOG.info("Removing duplicates");
            logger.newLine();
            stripDuplicatesFromFile(domainEntitiesPath);

            logger.write("Get categories with the Linked Data Recommender");
            LOG.info("Get categories with the Linked Data Recommender");
            logger.newLine();
            getDomainEntities(ldrEndpoint, domainEntitiesPath, node);

            logger.write("Get RDF representation of entities...");
            LOG.info("Get RDF representation of entities...");
            logger.newLine();

            Model model = getRDF(tmfDomainQuery, domainEntitiesPath, node);

        }
        logger.close();
        writer.close();
    }

    private static int appendEntities(BufferedWriter writer,
                                          List entitiesList,
                                          String baseuri) throws IOException {
        int appended = 0;
        Iterator<String> iterator = entitiesList.iterator();
        while (iterator.hasNext()) {
            String entity = iterator.next();
            writer.write(entity.split(baseuri)[1]);
            writer.newLine();
            appended += 1;
        }
        return appended;
    }

    private static Model getRDF(TMFDomainQuery tmfDomainQuery, String entitiesListPath, JsonNode node) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        String endpoint = getValue("endpoint", node);
        String baseuri = getValue("baseuri", node);
        BufferedReader reader = new BufferedReader(new FileReader(entitiesListPath));
        String entity;
        while(true) {
            entity = reader.readLine();
            if(entity == null)
                break;
            model.add(tmfDomainQuery.getRDFRepresentation(entity, baseuri, endpoint));
        }
        return model;
    }

    private static void getDomainEntities(String ldrEndpoint, String filename, JsonNode node) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String baseuri = getValue("baseuri", node);
        String[] clientParameters = new String[2];
        clientParameters[0] = baseuri;
        LDRClient ldrClient = new LDRClient(ldrEndpoint);
        String uri = reader.readLine();
        while (uri != null) {
            clientParameters[1] = uri;
            ldrClient.getDomainEntities(clientParameters);
            uri = reader.readLine();
        }
        reader.close();
    }

    public static void stripDuplicatesFromFile(String filename) throws IOException {
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

    private static String getValue (String string, JsonNode record) {
        return record.get(string) != null ? record.get(string).asText() : "";
    }
}

package org.dbpedia.spotlight.lucene.index.external.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * TODO --> Description of the class
 *
 */
public class TMFDomainEngine {

    final static Log LOG = LogFactory.getLog(TMFDomainEngine.class);

    public static void main(String[] args) throws IOException {

        LOG.info("Start to get domain entities...");

        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        String domainConfPath = config.getProperty("tellmefirst.domain.conf", "").trim();
        String domainLoggerPath = config.getProperty("tellmefirst.domain.logger", "").trim();
        String domainEntitiesPath = config.getProperty("org.dbpedia.spotlight.data.conceptURIs", "").trim();

        List allEntitiesList = new ArrayList();

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
            int oldNumberOfEntities = allEntitiesList.size();

            logger.write("Query description: " + getValue("description", node));
            LOG.info("Query description: " + getValue("description", node));
            logger.newLine();

            logger.write("Language: " + getValue("language", node));
            LOG.info("Language: " + getValue("language", node));
            logger.newLine();

            logger.write("Number of entities: " + oldNumberOfEntities);
            LOG.info("Number of entities: " + oldNumberOfEntities);
            logger.newLine();

            TMFDomainQuery tmfDomainQuery = new TMFDomainQuery(getValue("query", node), getValue("endpoint", node));
            int offset = 0;
            Boolean appended = true;

            while (appended) {

                // TODO Add try/catch
                appended = appendEntities(writer, allEntitiesList, tmfDomainQuery.getEntities(offset), getValue("baseuri", node));
                offset += 10000; // This is the default number of rows returned by online endpoints
            }

            int numOfAddedEntities = allEntitiesList.size() - oldNumberOfEntities;
            logger.write("Added " + numOfAddedEntities + " entities");
            LOG.info("Added " + numOfAddedEntities + " entities");
            logger.newLine();
        }
        logger.close();
        writer.close();
    }

    private static Boolean appendEntities(BufferedWriter writer,
                                          List allEntitiesList,
                                          List entitiesList,
                                          String baseuri) throws IOException {

        Boolean appended = false;
        Iterator<String> iterator = entitiesList.iterator();
        while (iterator.hasNext()) {
            String entity = iterator.next();
            if (!allEntitiesList.contains(entity)) {
                allEntitiesList.add(entity);
                writer.write(entity.split(baseuri)[1]);
                writer.newLine();
                appended = true;
            }
        }
        return appended;
    }

    private static String getValue (String string, JsonNode record) {
        return record.get(string) != null ? record.get(string).asText() : "";
    }
}

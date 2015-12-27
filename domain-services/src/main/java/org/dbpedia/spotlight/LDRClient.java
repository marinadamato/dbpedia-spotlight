package org.dbpedia.spotlight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.*;

public class LDRClient implements DomainServiceClient{

    private static String endpoint;

    public LDRClient(String ep) {
        endpoint = ep;
    }

    public List getDomainEntities(String[] parameters) throws Exception {
        List entitiesList = new ArrayList();
        // The first parameter is the base uri
        // The second parameter is the DBpedia resource
        if(parameters.length == 2){
            getCategories(parameters[0], parameters[1], endpoint);
        }
        else throw new Exception("Number of parameters is wrong!");
        return entitiesList;
    }

    private List getCategories(String baseuri, String resource, String endpoint) throws IOException {
        List categoriesList = new ArrayList();
        HttpClientWrapper httpClientWrapper = new HttpClientWrapper();
        String request = endpoint + "?uri=" + baseuri + resource;
        String inputJSON = httpClientWrapper.executeRequest(request);
        if (inputJSON.length() != 0) {
            Map scoringCategories = extractCategoriesWithScoring(inputJSON);
            categoriesList = saveCategoriesInList(scoringCategories);
        }
        return categoriesList;
    }

    private Map extractCategoriesWithScoring(String inputJSON) throws JsonParseException, IOException {
        Map scoringCategories = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readValue(inputJSON, JsonNode.class);
        int i = 0;
        while(node.get(i) != null){
            scoringCategories.put(getValue("uri", node.get(i)) , getValue("distance", node.get(i)));
            i++;
        }
        Map<String, String> orderedMap = new TreeMap<String, String>(scoringCategories);
        return orderedMap;
    }

    private List saveCategoriesInList(Map<String, String> map) {
        List categoriesList = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            categoriesList.add(entry.getKey());
        }
        return categoriesList;
    }

    private void printMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }

    private static String getValue (String string, JsonNode record) {
        return record.get(string) != null ? record.get(string).asText() : "";
    }
}

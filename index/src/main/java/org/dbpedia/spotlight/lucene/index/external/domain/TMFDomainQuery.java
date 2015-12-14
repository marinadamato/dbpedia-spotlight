package org.dbpedia.spotlight.lucene.index.external.domain;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.*;

/**
 *
 * This class executes a general SPARQL query to an endpoint
 * It retrieves domain entities specified in a configuration
 * It publishes the RDF representation on a domain endpoint
 *
 */
public class TMFDomainQuery {

    private String sparqlQuery;
    private String sparqlEndpoint;

    public TMFDomainQuery(String query, String endpoint) {
        sparqlQuery = query;
        sparqlEndpoint = endpoint;
    }

    public List getEntities(int offset) {
        List entitiesList = new ArrayList();
        Query query = QueryFactory.create(sparqlQuery + " OFFSET " + offset);

        // TODO Add info to the standard output

        System.out.println(query);


        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
        ResultSet results = qexec.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();

            // TODO Understand if you get all results from the endpoint SPARQL

            // TODO Remember that you want to get an RDF representation of each entity

            // TODO Check if you have to clean

            entitiesList.add(result.get("entity").toString());
        }

        qexec.close();
        return entitiesList;
    }

    private static void getRDFRepresentation(int offset) {
        // TODO
    }

    public void publishOnEndpoint() {
        // TODO Define a specific implementation for BlazeGraph triple store.
        // TODO Publish through HTTP

    }
}

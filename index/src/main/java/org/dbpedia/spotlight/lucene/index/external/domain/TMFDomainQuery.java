package org.dbpedia.spotlight.lucene.index.external.domain;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.util.FileManager;

/**
 *
 * This class executes a general SPARQL query to an endpoint
 * It retrieves domain entities specified in a configuration
 * It publishes the RDF representation on a domain endpoint
 *
 */
public class TMFDomainQuery {

    public List getEntities(String sparqlQuery, String sparqlEndpoint, int offset) {
        List entitiesList = new ArrayList();
        Query query = QueryFactory.create(sparqlQuery + " OFFSET " + offset);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.next();
            entitiesList.add(result.get("entity").toString());
        }
        qexec.close();
        return entitiesList;
    }

    public static Model getRDFRepresentation(String entity, String baseuri, String endpoint) {
        FileManager fileManager = new FileManager();
        Model describeModel = fileManager.loadModel(baseuri + entity + ".rdf");
        return describeModel;
    }

    public void publishOnEndpoint(String endpoint) {
        // TODO Define a specific implementation for BlazeGraph triple store.
        // TODO Publish through HTTP

    }
}

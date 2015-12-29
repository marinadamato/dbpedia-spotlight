package org.dbpedia.spotlight.lucene.index.external.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 *
 * This class executes a general SPARQL query to an endpoint
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

    public Model getRDFRepresentation(String entity, String baseuri) {
        FileManager fileManager = new FileManager();
        try {
            return fileManager.loadModel(baseuri + entity + ".rdf");
        }
        finally {
            return ModelFactory.createDefaultModel();
        }
    }

    public void publishRDFOnFileSystem(Model model, String outputPath) throws FileNotFoundException {
        File file = new File(outputPath.replaceAll("(.+)/[^/]+", "$1"));
        file.mkdirs();
        OutputStream outTurtle = new FileOutputStream(new File(outputPath));
        RDFDataMgr.write(outTurtle, model, RDFFormat.NTRIPLES);
    }

    public void publishOnEndpoint(String endpoint) {
        // TODO Define a specific implementation for BlazeGraph triple store.
        // TODO Publish through HTTP

    }
}

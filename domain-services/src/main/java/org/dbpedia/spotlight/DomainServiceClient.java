package org.dbpedia.spotlight;

import java.util.List;

/**
 * Interface for a basic request to a domain service
 *
 */
public interface DomainServiceClient {

    public List getDomainEntities(String[] parameters) throws Exception;
}

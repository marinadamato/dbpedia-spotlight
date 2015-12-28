package org.dbpedia.spotlight;

import eu.fusepool.p3.transformer.HttpRequestEntity;
import eu.fusepool.p3.transformer.SyncTransformer;
import eu.fusepool.p3.transformer.commons.Entity;
import eu.fusepool.p3.transformer.commons.util.WritingEntity;
import eu.fusepool.p3.transformer.server.TransformerServer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 * This class implements features of Fusepool transformer
 * in order to standardize services to get new entities for
 * TellMeFirst index.
 *
 * The implementation of this transformer wraps the REST service
 * of the Linked Data Recommender developed by the SoftEng research
 * group, Politecnico di Torino (DAUIN) - http://softeng.polito.it/
 *
 */
public class LDRService implements SyncTransformer {

    private static enum Transformer {
        sync, async
    }

    private static final Logger fLogger = LoggerFactory.getLogger(LDRService.class);

    public static final String PAR_SCRIPT = "script";

    private static final MimeType MIME_TEXT_PLAIN = mimeType("text", "plain");

    private static final Transformer fType = Transformer.sync;

    private static String ldrEndpoint;

    @SuppressWarnings("serial")
    private static final Set<MimeType> INPUT_FORMATS = Collections
            .unmodifiableSet(new HashSet<MimeType>() {{
                add(MIME_TEXT_PLAIN);
            }});

    private static final Set<MimeType> OUTPUT_FORMATS = INPUT_FORMATS;


    @Override
    public Set<MimeType> getSupportedInputFormats() {
        return INPUT_FORMATS;
    }

    @Override
    public Set<MimeType> getSupportedOutputFormats() {
        return OUTPUT_FORMATS;
    }

    @Override
    public Entity transform(HttpRequestEntity entity) throws IOException {
        // Put the implementation of your service here here
        fLogger.info("Get new entities with SoftEng Linked Data Recommender");
        String inputData = IOUtils.toString(entity.getData());

        // TODO Manage different kind or requests

        HttpClientWrapper httpClientWrapper = new HttpClientWrapper();
        String transformed = httpClientWrapper.executeRequest(ldrEndpoint + "?uri=" + inputData);
        return wrapInEntity(transformed);
    }

    private WritingEntity wrapInEntity(final String transformed) {
        return new WritingEntity() {
            @Override
            public MimeType getType() {
                return MIME_TEXT_PLAIN;
            }

            @Override
            public void writeData(OutputStream out) throws IOException {
                out.write(transformed.getBytes());
            }
        };
    }

    @Override
    public boolean isLongRunning() {
        return false;
    }

    private static MimeType mimeType(String primary, String sub) {

        try {
            return new MimeType(primary, sub);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException("Internal error.");
        }
    }

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        ldrEndpoint = config.getProperty("tellmefirst.domain.LDRService", "").trim();
        int fPort = Integer.parseInt(config.getProperty("tellmefirst.domain.LDRService.port", "").trim());

        TransformerServer server = new TransformerServer(fPort, false);
        if (fType.equals(Transformer.sync)) {
            server.start(new LDRService());
            fLogger.info("SoftEng Linked Data Recommender is up!");
        }
        try {
            server.join();
        } catch (InterruptedException ex) {
            fLogger.error("Internal error: ", ex);
            Thread.currentThread().interrupt();
        }
    }
}

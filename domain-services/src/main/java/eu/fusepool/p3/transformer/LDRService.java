package eu.fusepool.p3.transformer;

import eu.fusepool.p3.transformer.commons.Entity;
import eu.fusepool.p3.transformer.commons.util.WritingEntity;
import eu.fusepool.p3.transformer.server.TransformerServer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
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

    @Option(name = "-p", aliases = {"--port"}, usage = "set the port to which to bind to", metaVar = "7100", required = false)
    private final int fPort = 7101;

    @Option(name = "-t", aliases = {"--type"}, usage = "specify transformer type (sync, async), default: sync", metaVar = "sync", required = false)
    private final Transformer fType = Transformer.sync;

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
        InputStream inputData = entity.getData();

        // Put the implementation of your service here here

        final String transformed = "";
        fLogger.info("Get new entities with SoftEng Linked Data Recommender");

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

    private String checkedGet(HttpRequestEntity entity, String parameter) {
        String par = entity.getRequest().getParameter(parameter);
        // If there's no regex, throws Exception
        if (par == null) {
            throw new RuntimeException("Missing parameter " + parameter + ".");
        }
        return par;
    }

    @Override
    public boolean isLongRunning() {
        return false;
    }

    private String charsetOf(HttpServletRequest request) {

        String encoding = request.getCharacterEncoding();

        if (encoding == null) {
            fLogger.error("Cannot resolve encoding " + encoding + ". Defaulting to US-ASCII.");
            encoding = "US-ASCII";
        }

        return encoding;
    }

    private static MimeType mimeType(String primary, String sub) {

        try {
            return new MimeType(primary, sub);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException("Internal error.");
        }
    }

    public void _main(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.out);
            System.exit(-1);
        }

        TransformerServer server = new TransformerServer(fPort, false);
        if (fType.equals(Transformer.sync))
            server.start(new LDRService());
        try {
            server.join();
        } catch (InterruptedException ex) {
            fLogger.error("Internal error: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        new LDRService()._main(args);
    }
}

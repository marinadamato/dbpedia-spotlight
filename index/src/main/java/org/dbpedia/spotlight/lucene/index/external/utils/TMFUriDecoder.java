/**
 * TellMeFirst - A Knowledge Discovery Application
 *
 * Copyright (C) 2014 Federico Cairo, Giuseppe Futia, Federico Benedetto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dbpedia.spotlight.lucene.index.external.utils;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class TMFUriDecoder {

    final static Log LOG = LogFactory.getLog(TMFUriDecoder.class);

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        decodeURI(config, "conceptURIsToDecode", "conceptURIs");
        decodeURI(config, "redirectsTCToDecode", "redirectsTC");
        decodeURI(config, "surfaceFormsToDecode", "surfaceForms");
    }

    public static void decodeURI(Properties config, String toDecode, String decoded){

        LOG.info("Start to decode URIs...");

        final BufferedReader reader;
        final BufferedWriter writer;
        final Path src = Paths.get(config.getProperty("org.dbpedia.spotlight.data." + toDecode, "").trim());
        final Path dst = Paths.get(config.getProperty("org.dbpedia.spotlight.data."+decoded, "").trim());
        String line;

        try {
            reader = Files.newBufferedReader(src, StandardCharsets.UTF_8);
            writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);

            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                line = line.replaceAll("\\+", "%2B");
                writer.write(URLDecoder.decode(line, "UTF-8"));
                writer.newLine();
            }
            writer.close();

            LOG.info("Done");

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}



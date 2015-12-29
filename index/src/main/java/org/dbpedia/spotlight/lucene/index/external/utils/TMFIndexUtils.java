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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class TMFIndexUtils {

    final static Log LOG = LogFactory.getLog(TMFIndexUtils.class);

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        //correctWikipediaDump(config);
        decodeURI(config, "conceptURIsToDecode", "conceptURIs");
        decodeURI(config, "redirectsTCToDecode", "redirectsTC");
        decodeURI(config, "surfaceFormsToDecode", "surfaceForms");
    }

    public static void correctWikipediaDump (Properties config) {

        LOG.info("Start to correct the Wikipedia dump...");

        String originalDumpPath = config.getProperty("org.dbpedia.spotlight.data.originalWikipediaDump", "").trim();
        String dumpPath = config.getProperty("org.dbpedia.spotlight.data.wikipediaDump", "").trim();

        final BufferedReader reader;
        final BufferedWriter writer;

        String line;

        try{
            reader = getBufferedReaderForBZ2File(originalDumpPath);
            writer = new BufferedWriter(new FileWriter(dumpPath));

            int rows = 0;
            while ((line = reader.readLine()) != null) {
                if(line.contains("<ns>118</ns>") ||
                        line.contains("<ns>446</ns>") ||
                        line.contains("<ns>447</ns>") ||
                        line.contains("<ns>710</ns>") ||
                        line.contains("<ns>711</ns>") ||
                        line.contains("<ns>828</ns>") ||
                        line.contains("<ns>829</ns>")
                        ){
                    LOG.info("Correct row "+rows+ ":" +line);
                    line = line.replaceAll("[0-9]+/*\\.*[0-9]*","0");
                    line = line.replaceAll("-", "");
                    writer.write(line);
                }else {
                    writer.write(line);
                }
                writer.newLine();
                rows += 1;
                if (rows %10000000 == 0) {
                    LOG.info("Processed " + rows +" rows");
                }
            }
            writer.close();
            LOG.info("Done");

        }catch (Exception e){
               e.printStackTrace();
        }
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

    public static BufferedReader getBufferedReaderForBZ2File(String fileIn) throws IOException, CompressorException {
        FileInputStream fin = new FileInputStream(fileIn);
        BZip2CompressorInputStream input = new BZip2CompressorInputStream(fin, true);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
        return br2;
    }
}

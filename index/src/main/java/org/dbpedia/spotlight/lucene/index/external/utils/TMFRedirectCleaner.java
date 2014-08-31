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
import java.util.Properties;


public class TMFRedirectCleaner {

    final static Log LOG = LogFactory.getLog(TMFRedirectCleaner.class);

    public static void main(String[] args) throws IOException, CompressorException {

        LOG.info("Start to clean redirect file...");

        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        String redirectPath = config.getProperty("org.dbpedia.spotlight.data.redirects", "").trim();
        String tempRedirectPath = config.getProperty("org.dbpedia.spotlight.data.redirects", "").trim()+"-temp";

        final BufferedReader reader = getBufferedReaderForBZ2File(redirectPath);
        final BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempRedirectPath));

        String line;

        //To improve in order to find all cases
        while ((line = reader.readLine()) != null) {
            //LOG.info(line);
            if(line.toString().contains("B_with_stroke")) {
                LOG.info("Remove "+line);
                continue;
            }
            tempWriter.write(line);
            tempWriter.newLine();
        }
        tempWriter.close();
        LOG.info("Done");
        LOG.info("Start to compress the new cleaned redirect file...");
        compressInBz2(redirectPath, tempRedirectPath, redirectPath+"new");
        LOG.info("Done");
    }

    public static BufferedReader getBufferedReaderForBZ2File(String fileIn) throws IOException, CompressorException {
        FileInputStream fin = new FileInputStream(fileIn);
        BZip2CompressorInputStream input = new BZip2CompressorInputStream(fin, true);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
        return br2;
    }

    public static void compressInBz2(String originalFile, String inputFile, String outFile) throws FileNotFoundException, IOException {
        final File original = new File(originalFile);
        final File source = new File(inputFile);
        final File destination = new File(outFile);
        final BZip2CompressorOutputStream output = new BZip2CompressorOutputStream(new FileOutputStream(destination));
        final FileInputStream input = new FileInputStream(source);
        final byte[] buffer = new byte[8024];
        int n = 0;
        while (-1 != (n = input.read(buffer)))      {
            output.write(buffer, 0, n);
        }
        input.close();
        output.close();
        if(source.delete()){
            LOG.info(source.getName() + " is deleted.");
        }else{
            LOG.error("Delete operation is failed.");
        }
        if(destination.renameTo(original)){
            LOG.info(destination.getName() + " is renamed.");
        }else {
            LOG.error("Rename operation is failed.");
        }
    }
}

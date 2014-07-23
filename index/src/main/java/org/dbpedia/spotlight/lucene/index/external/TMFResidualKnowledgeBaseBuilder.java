package org.dbpedia.spotlight.lucene.index.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.dbpedia.spotlight.exceptions.ConfigurationException;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: giuseppe
 * Date: 23/07/14
 * Time: 19.31
 * To change this template use File | Settings | File Templates.
 */
public class TMFResidualKnowledgeBaseBuilder {
    final static Log LOG = LogFactory.getLog(TMFResidualKnowledgeBaseBuilder.class);

    private IndexReader reader;
    private IndexWriter writer;

    public int countLines(String fileName) throws IOException {
        String line;
        int lineCount = 0;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((line = br.readLine()) != null) {
            lineCount++;
        }
        br.close();
        return lineCount;
    }

    public String cleanUri(String s) {
        // for Italian
        //return s.replace("<http://it.dbpedia.org/resource/", "").replace(">", "").trim();
        return s.replace("<http://dbpedia.org/resource/", "").replace(">", "").trim();
    }

    public ArrayList<String> cleanUriList(ArrayList<String> list){
        ArrayList<String> result= new ArrayList<String>();
        for  (String s : list){
            result.add(cleanUri(s));
        }
        return result;
    }


    public void addADocument(String uri, String wikilinks) throws IOException, ConfigurationException {
        Document doc = new Document();
        Field uriField = new Field("URI", uri, Field.Store.YES, Field.Index.NOT_ANALYZED);
        doc.add(uriField);
        Field kbField = new Field("KB", wikilinks, Field.Store.YES, Field.Index.NOT_ANALYZED);
        doc.add(kbField);
        writer.addDocument(doc);
    }

    public void deleteFieldFromIndex(String fieldName, int docId, Analyzer analyzer) throws IOException, ConfigurationException {
        Document doc = reader.document(docId);
        doc.removeFields(fieldName);
        Field uri = doc.getField("URI");
        Term term = new Term("URI", uri.stringValue());
        writer.updateDocument(term, doc, analyzer);
    }

    public LinkedHashMap sortHashMap(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.reverse(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap =
                new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)){
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String)key, (Integer)val);
                    break;
                }
            }
        }
        return sortedMap;
    }


    public String filterWikiList(ArrayList<String> wikilinks) throws IOException {
        ArrayList<String> cleanedWikilinks = cleanUriList(wikilinks);
        ArrayList<String> newArray = new ArrayList<String>();
        for(String wiki : cleanedWikilinks){
            if(!wiki.startsWith("Category:") && !wiki.startsWith("Rue:") && !wiki.startsWith("File:")
                    && !wiki.startsWith("Portal:") && !wiki.startsWith("Template:")){
                newArray.add(wiki);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(String wikilinkUri : newArray){
            sb.append(wikilinkUri+" ");
        }
        return sb.toString();
    }


    public static void main(String[] args) throws IOException, ConfigurationException {
        long start = System.currentTimeMillis();
        TMFResidualKnowledgeBaseBuilder kbb = new TMFResidualKnowledgeBaseBuilder();

        Properties config = new Properties();
        String confFile = args[0];
        config.load(new FileInputStream(new File(confFile)));
        // add this properties to indexing.properties
        String tmfResidualKBPath = config.getProperty("tellmefirst.residualkb", "").trim();
        String wikilinksFilePath = config.getProperty("tellmefirst.wikilinks", "").trim();

        //older implmentation of IndexWriter
        //kbb.writer = new IndexWriter(residualKBPath, new StandardAnalyzer(), true);

        StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_36);
        Directory directory = FSDirectory.open(new File(tmfResidualKBPath));
        kbb.writer = new IndexWriter(directory, sa, true, new IndexWriter.MaxFieldLength(25000));

        int numLines = kbb.countLines(wikilinksFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(wikilinksFilePath));
        String line = bufferedReader.readLine();
        LOG.info("Ignore the first line: "+line);
        line =  bufferedReader.readLine();
        String uriPresent = line.split(" ")[0];
        String uriPast = uriPresent;
        ArrayList<String> wikilinks = new ArrayList<String>();
        for(int i = 0; i<numLines; i++){
            if(i != numLines-1){
                if ((uriPresent.equals(uriPast))) {
                    wikilinks.add(line.split(" ")[2]);
                }
                else {
                    String mergedWikilist = kbb.filterWikiList(wikilinks);
                    kbb.addADocument(kbb.cleanUri(uriPast), mergedWikilist);
                    wikilinks = new ArrayList<String>();
                    wikilinks.add(line.split(" ")[2]);
                }
                line =  bufferedReader.readLine();
                uriPast = uriPresent;
                uriPresent =  line.split(" ")[0];
            }
            else{
                wikilinks.add(line.split(" ")[2]);
                String mergedWikilist = kbb.filterWikiList(wikilinks);
                kbb.addADocument(kbb.cleanUri(uriPast), mergedWikilist);
            }
        }
        bufferedReader.close();
        kbb.writer.close();
        long stop = System.currentTimeMillis();
        long time = (stop - start)/(60*1000);
        System.out.println("Time elapsed: "+time+" minutes.");
    }
}

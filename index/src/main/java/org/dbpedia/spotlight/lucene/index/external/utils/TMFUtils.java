package org.dbpedia.spotlight.lucene.index.external.utils;

import org.apache.lucene.search.ScoreDoc;
import org.dbpedia.spotlight.exceptions.ConfigurationException;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: giuseppe
 * Date: 18/07/14
 * Time: 10.58
 * To change this template use File | Settings | File Templates.
 */
public class TMFUtils {

    public static boolean commonWords = true;


    public static ArrayList<String> getItalianStopWords(String confFile) throws ConfigurationException {

        ArrayList<String> italianStopWords = new ArrayList<String>();
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String italianStopWordsFile = config.getProperty("tellmefirst.stopWords");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(italianStopWordsFile.trim()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                italianStopWords.add(line.trim());
            }
            bufferedReader.close();
        } catch (Exception e1) {
            System.out.println("Could not read italian stopwords file.");
        }
        return italianStopWords;

    }

    public static ArrayList<String> getEnglishStopWords(String confFile) throws ConfigurationException {

        ArrayList<String> italianStopWords = new ArrayList<String>();
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String italianStopWordsFile = config.getProperty("tellmefirst.english.stopWords");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(italianStopWordsFile.trim()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                italianStopWords.add(line.trim());
            }
            bufferedReader.close();
        } catch (Exception e1) {
            System.out.println("Could not read english stopwords file.");
        }
        return italianStopWords;

    }

    public static String getKBPath(String confFile) throws ConfigurationException {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String result = config.getProperty("tellmefirst.kb");
        return  result;
    }

    public static String getResidualKBPath(String confFile) throws ConfigurationException {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String result = config.getProperty("tellmefirst.residualkb");
        return  result;
    }



    public static ArrayList<String> getCommonWordsMin(String confFile) throws ConfigurationException {

        ArrayList<String> commonWords = new ArrayList<String>();
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String italianStopWordsFile = config.getProperty("tellmefirst.commonWords.min");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(italianStopWordsFile.trim()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                commonWords.add(line.trim());
            }
            bufferedReader.close();
        } catch (Exception e1) {
            System.out.println("Could not read common words file.");
        }
        return commonWords;

    }

    public static ArrayList<String> getCommonWordsMax(String confFile) throws ConfigurationException {

        ArrayList<String> commonWords = new ArrayList<String>();
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String italianStopWordsFile = config.getProperty("tellmefirst.commonWords.max");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(italianStopWordsFile.trim()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                commonWords.add(line.trim());
            }
            bufferedReader.close();
        } catch (Exception e1) {
            System.out.println("Could not read common words file.");
        }
        return commonWords;

    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(.\\d+)?");
    }


    public static boolean uriStartsWithArticle (String str){
        String[] articles = {"Il_", "Lo_", "La_", "I_", "Gli_", "Le_", "Un_", "Uno_", "Una_", "Un'", "L'"};
        for (int i = 0; i<9; i++){
            if (str.startsWith(articles[i])){
                return true;
            }

        }
        return false;
    }




    public static double convertConfidenceToSimilarity(double confidence){
        double [] tmfSimilarities = {0, 0.04, 0.08, 0.12, 0.16, 0.2, 0.5, 1, 2, 3};
        return tmfSimilarities[(int)(confidence*10)];
    }


    public static String deleteArticlesFromBeginning(String s){
        String result = s;
        //FEDE TODO: valutare se è il caso di aggiungere le preposizioni
        String[] articles = {"Il ", "il ", "Lo ", "lo ",  "La ", "la ", "I ", "i ", "Gli ", "gli ", "Le ", "le ", "L'", "l'", "Un ", "un ", "Uno ", "uno ", "Una ", "una ", "Un'", "un'", "In ", "in "};
        for(String article : articles)
            if (s.startsWith(article)) {
                result = s.replaceFirst(article,"");
            }
        return result;
    }

    public static ScoreDoc[] removeFirstElementWithReturn(ScoreDoc[] input) {
        ArrayList<ScoreDoc> result = new ArrayList<ScoreDoc>();

        for(int i=0; i<input.length; i++) {
            result.add(input[i]);
        }

        result.remove(0);

        ScoreDoc[] newArray = new ScoreDoc[result.size()];
        for(int i=0; i<result.size(); i++){
            newArray[i]= result.get(i);
        }

        return newArray;
    }

    //questo metodo lascia alla fine dell'array degli 0
    public static void removeFirstElement(Object[] a) {
        System.arraycopy(a,0+1,a,0,a.length-1-0);
        a[a.length-1] = null;
    }


    public static int countNotNullInArray(Object[] anArray){
        int count = 0;
        for (Object o : anArray){
            if(o != null){
                count++;
            }
        }
        return count;
    }

    public static int countNullInArray(Object[] anArray){
        int count = 0;
        for (Object o : anArray){
            if(o == null){
                count++;
            }
        }
        return count;
    }

    public static LinkedHashMap sortHashMap(HashMap passedMap) {
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

    public static LinkedHashMap sortHashMapIntegers(HashMap passedMap) {
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
                    sortedMap.put((Integer)key, (Integer)val);
                    break;
                }

            }

        }
        return sortedMap;
    }



    public static String getCorpusIndexPath(String confFile) throws ConfigurationException {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(new File(confFile)));
        } catch (IOException e) {
            throw new ConfigurationException("Cannot find configuration file "+confFile,e);
        }
        String result = config.getProperty("org.dbpedia.spotlight.index.dir");
        return  result;
    }


    public static int countWords (String in) {
        String[] words = in.split(" "); //separate string around spaces
        return words.length;
    }


}

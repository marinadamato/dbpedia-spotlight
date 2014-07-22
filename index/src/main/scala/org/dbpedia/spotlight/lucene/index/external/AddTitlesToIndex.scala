package org.dbpedia.spotlight.lucene.index.external

import org.dbpedia.spotlight.util.{TypesLoader, IndexingConfiguration}
import org.dbpedia.spotlight.lucene.index.IndexEnricher
import java.io.FileInputStream

/**
 * Created with IntelliJ IDEA.
 * User: utente
 * Date: 07/12/12
 * Time: 8.51
 * To change this template use File | Settings | File Templates.
 */
object AddTitlesToIndex {
  def loadTitles(instanceTypesFileName: String) = {
    instanceTypesFileName.endsWith(".tsv") match {
      //case true  => TitlesLoader.getTypesMapFromTSV_java(new File(instanceTypesFileName))
      case true => TypesLoader.getTitlesMap_java(new FileInputStream(instanceTypesFileName))
      case false => TypesLoader.getTitlesMap_java(new FileInputStream(instanceTypesFileName))
    }
  }

  def main(args: Array[String]) {
    println("Reindexing...")
    val indexingConfigFileName = args(0)
    val sourceIndexFileName = args(1)

    val config = new IndexingConfiguration(indexingConfigFileName)
    val targetIndexFileName = config.get("tellmefirst.index_with_titles")
    val instanceTypesFileName = config.get("tellmefirst.titles")

    val typesIndexer = new IndexEnricher(sourceIndexFileName, targetIndexFileName, config)

    val titleMap = loadTitles(instanceTypesFileName)
    typesIndexer.enrichWithTitlesEnglish(titleMap)
    typesIndexer.close
  }


}

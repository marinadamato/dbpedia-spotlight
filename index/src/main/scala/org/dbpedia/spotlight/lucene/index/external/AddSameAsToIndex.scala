package org.dbpedia.spotlight.lucene.index.external

import org.dbpedia.spotlight.util.{IndexingConfiguration, TypesLoader}
import java.io.FileInputStream
import org.dbpedia.spotlight.lucene.index.IndexEnricher

/**
 * Created with IntelliJ IDEA.
 * User: utente
 * Date: 11/12/12
 * Time: 8.01
 * To change this template use File | Settings | File Templates.
 */
object AddSameAsToIndex {

  def loadTitles(instanceTypesFileName: String) = {
    println("fede1")
    instanceTypesFileName.endsWith(".tsv") match {
      //case true  => TitlesLoader.getTypesMapFromTSV_java(new File(instanceTypesFileName))
      case true => TypesLoader.getSomeAsMap_java(new FileInputStream(instanceTypesFileName))
      case false => TypesLoader.getSomeAsMap_java(new FileInputStream(instanceTypesFileName))
    }
  }

  def main(args: Array[String]) {
    println("Iniziato processo di reindicizzazione...")
    val indexingConfigFileName = args(0)
    val sourceIndexFileName = args(1)

    val config = new IndexingConfiguration(indexingConfigFileName)
    val targetIndexFileName = config.get("tellmefirst.index_with_titles_images_sameAs")
    val instanceTypesFileName = config.get("tellmefirst.sameAs")

    val typesIndexer = new IndexEnricher(sourceIndexFileName, targetIndexFileName, config)

    val titleMap = loadTitles(instanceTypesFileName)
    typesIndexer.enrichWithSameAs(titleMap)
    typesIndexer.close
  }

}

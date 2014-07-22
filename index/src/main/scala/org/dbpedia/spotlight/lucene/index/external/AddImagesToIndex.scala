package org.dbpedia.spotlight.lucene.index.external

import org.dbpedia.spotlight.util.{IndexingConfiguration, TypesLoader}
import java.io.FileInputStream
import org.dbpedia.spotlight.lucene.index.IndexEnricher

/**
 * Created with IntelliJ IDEA.
 * User: utente
 * Date: 07/12/12
 * Time: 11.53
 * To change this template use File | Settings | File Templates.
 */
object AddImagesToIndex {

  def loadImages(instanceTypesFileName: String) = {
    println("fede1")
    instanceTypesFileName.endsWith("en.nt") match {
      //case true  => TitlesLoader.getTypesMapFromTSV_java(new File(instanceTypesFileName))
      case true => TypesLoader.getImagesMap_java(new FileInputStream(instanceTypesFileName))
      case false => TypesLoader.getImagesMap_italian_java(new FileInputStream(instanceTypesFileName))
    }
  }

  def main(args: Array[String]) {
    println("Iniziato processo di reindicizzazione...")
    val indexingConfigFileName = args(0)
    val sourceIndexFileName = args(1)

    val config = new IndexingConfiguration(indexingConfigFileName)
    val targetIndexFileName = config.get("tellmefirst.index_with_titles_images")
    val instanceTypesFileName = config.get("tellmefirst.images")

    val typesIndexer = new IndexEnricher(sourceIndexFileName, targetIndexFileName, config)

    val titleMap = loadImages(instanceTypesFileName)
    typesIndexer.enrichWithImagesEnglish(titleMap)
    typesIndexer.close
  }

}

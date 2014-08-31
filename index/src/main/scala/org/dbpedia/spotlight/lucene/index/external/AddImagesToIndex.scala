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

package org.dbpedia.spotlight.lucene.index.external

import org.dbpedia.spotlight.util.{IndexingConfiguration, TypesLoader}
import java.io.FileInputStream
import org.dbpedia.spotlight.lucene.index.IndexEnricher
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

object AddImagesToIndex {

  def loadImages(imagesFileName: String) = {
    imagesFileName.endsWith("en.nt.bz2") match {
      case true => TypesLoader.getImagesMap_java(new BZip2CompressorInputStream(new FileInputStream(imagesFileName), true))
      case false => TypesLoader.getImagesMap_italian_java(new BZip2CompressorInputStream(new FileInputStream(imagesFileName), true))
    }
  }

  def main(args: Array[String]) {
    val indexingConfigFileName = args(0)
    val sourceIndexFileName = args(1)

    val config = new IndexingConfiguration(indexingConfigFileName)
    val targetIndexFileName = config.get("tellmefirst.index_with_titles_images")
    val imagesFileName = config.get("tellmefirst.images")

    val typesIndexer = new IndexEnricher(sourceIndexFileName, targetIndexFileName, config)

    val titleMap = loadImages(imagesFileName)
    typesIndexer.enrichWithImagesEnglish(titleMap)
    typesIndexer.close
  }

}

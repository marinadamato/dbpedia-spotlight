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

import org.dbpedia.spotlight.util.{TypesLoader, IndexingConfiguration}
import org.dbpedia.spotlight.lucene.index.IndexEnricher
import java.io.FileInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

object AddTitlesToIndex {
  def loadTitles(labelsFileName: String) = {
    val input = new BZip2CompressorInputStream(new FileInputStream(labelsFileName), true)
    var titleMap = TypesLoader.getTitlesMap_java(input)
    input.close()
    titleMap
  }

  def main(args: Array[String]) {
    val indexingConfigFileName = args(0)
    val sourceIndexFileName = args(1)

    val config = new IndexingConfiguration(indexingConfigFileName)
    val targetIndexFileName = config.get("tellmefirst.index_with_titles")
    val labelsFileName = config.get("org.dbpedia.spotlight.data.labels")

    val titlesIndexer = new IndexEnricher(sourceIndexFileName, targetIndexFileName, config)

    val titleMap = loadTitles(labelsFileName)
    titlesIndexer.enrichWithTitlesEnglish(titleMap)
    titlesIndexer.close
  }


}

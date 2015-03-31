package org.dbpedia.spotlight.evaluation

import org.dbpedia.spotlight.spot.Spotter
import org.dbpedia.spotlight.corpus.{CSAWCorpus, MilneWittenCorpus}
import java.io.File
import org.dbpedia.spotlight.io.AnnotatedTextSource
import org.dbpedia.spotlight.spot.lingpipe.LingPipeSpotter
import com.aliasi.dict.{DictionaryEntry, MapDictionary}
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.util.Version
import org.dbpedia.spotlight.model.{Factory, SurfaceFormOccurrence}
import collection.JavaConversions
import org.apache.lucene.analysis._
import org.dbpedia.spotlight.log.SpotlightLog
import org.apache.lucene.analysis.standard.{StandardAnalyzer, ClassicAnalyzer}

/**
 *
 *
 */
object EvalSpotter {

  def evalCorpus = {
    MilneWittenCorpus.fromDirectory(new File("/home/max/spotlight-data/milne-witten"))
    //AnnotatedTextSource.fromOccurrencesFile(new File("/home/max/spotlight-data/CSAWoccs.red-dis-3.7-sorted.tsv")))
  }

    def main(args: Array[String]) {
        evalSpotting(MilneWittenCorpus.fromDirectory(new File("/home/max/spotlight-data/milne-witten")))
        //evalSpotting(AnnotatedTextSource.fromOccurrencesFile(new File("/home/max/spotlight-data/CSAWoccs.red-dis-3.7-sorted.tsv")))
    }

    def evalSpotting(annotatedTextSource: AnnotatedTextSource) {
        val analyzers = List(
            new SimpleAnalyzer(Version.LUCENE_36),
            new StopAnalyzer(Version.LUCENE_36),
            new ClassicAnalyzer(Version.LUCENE_36),
            new StandardAnalyzer(Version.LUCENE_36),
            new EnglishAnalyzer(Version.LUCENE_36),
            new WhitespaceAnalyzer(Version.LUCENE_36)
        )
        for (analyzer <- analyzers) {
            evalSpotting(annotatedTextSource, analyzer)
        }
    }

    def evalSpotting(annotatedTextSource: AnnotatedTextSource, analyzer: Analyzer) {
        // create gold standard and index
        var expected = Set[SurfaceFormOccurrence]()
        val dictionary = new MapDictionary[String]()
        for (paragraph <- annotatedTextSource;
             occ <- paragraph.occurrences) {
            expected += Factory.SurfaceFormOccurrence.from(occ)
            dictionary.addEntry(new DictionaryEntry[String](occ.surfaceForm.name, ""))
        }
        val lingPipeSpotter: Spotter = new LingPipeSpotter(dictionary, analyzer)

  def getExpectedResult(annotatedTextSource: AnnotatedTextSource) = {
    annotatedTextSource.foldLeft(Set[SurfaceFormOccurrence]()){ (set, par) =>
      set ++ par.occurrences.map(Factory.SurfaceFormOccurrence.from(_))
    }
  }

  def evalSpotter(annotatedTextSource: AnnotatedTextSource,
                  spotter: Spotter,
                  expected: Traversable[SurfaceFormOccurrence]) {

    // run spotting
    var actual = Set[SurfaceFormOccurrence]()
    for (paragraph <- annotatedTextSource) {
      actual = JavaConversions.asScalaBuffer(spotter.extract(paragraph.text)).toSet union actual
    }

    // compare
    printResults("%s and corpus %s".format(spotter.getName, annotatedTextSource.name), expected, actual)
  }


  private def evalSpotting(annotatedTextSource: AnnotatedTextSource,
                           indexSpotter: Traversable[SurfaceForm] => Spotter,
                           expected: Traversable[SurfaceFormOccurrence]) {
    // index spotter
    val spotter = indexSpotter(expected.map(_.surfaceForm))

        printResults("LingPipeSpotter with %s and corpus %s".format(analyzer.getClass, annotatedTextSource.name),
            expected, actual)
    }

    // compare
    printResults("%s and corpus %s".format(spotter.getName, annotatedTextSource.name), expected, actual)
  }

  private def printResults(description: String, expected: Traversable[SurfaceFormOccurrence], actual: Set[SurfaceFormOccurrence]) {
    var truePositive = 0
    var falseNegative = 0
    for (e <- expected) {
      if (actual contains e) {
        truePositive += 1
      } else {
        falseNegative += 1
        SpotlightLog.debug(this.getClass, "false negative: %s", e)
      }
    }
    val falsePositive = actual.size - truePositive

        val precision = truePositive.toDouble / (truePositive + falseNegative)
        val recall = truePositive.toDouble / (truePositive + falsePositive)

    SpotlightLog.info(this.getClass, description)
    SpotlightLog.info(this.getClass, "           | actual Y  | actual N")
    SpotlightLog.info(this.getClass, "expected Y |   %3d     |    %3d", truePositive, falseNegative)
    SpotlightLog.info(this.getClass, "expected N |   %3d     |    N/A", falsePositive)
    SpotlightLog.info(this.getClass, "precision: %f  recall: %f", precision, recall)
    SpotlightLog.info(this.getClass, "--------------------------------")
  }

}

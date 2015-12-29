# You are expected to run the commands in this script from inside the bin directory in your DBpedia Spotlight installation
# Adjust the paths here if you don't. This script is meant more as a step-by-step guidance than a real automated run-all.
# If this is your first time running the script, we advise you to copy/paste commands from here, closely watching the messages
# and the final output.
#
# @author maxjakob, pablomendes (modified by Federico Cairo and Giuseppe Futia)
#
# NOTICE: before starting the indexing process check comments in SpotlightConfiguration.java and in FileOccurrenceSource.scala

export DBPEDIA_WORKSPACE=../data/tellmefirst/dbpedia/en

export INDEX_CONFIG_FILE=../conf/indexing.tmf.en.properties

JAVA_XMX=16g


# you have to run maven from the module that contains the indexing classes
cd ../index
# the indexing process will generate files in the directory below
#if [ -e $DBPEDIA_WORKSPACE/output  ]; then
#    echo "$DBPEDIA_WORKSPACE"'/output already exist.'
#else
#    mkdir -p $DBPEDIA_WORKSPACE/output
#fi

# clean redirect file: there is a bug in the DBpedia version 3.9 (added by Giuseppe Futia)
#mvn compile
#mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.lucene.index.external.utils.TMFRedirectCleaner" -Dexec.args=$INDEX_CONFIG_FILE

#clean the Wikipedia Dump (added by Giuseppe Futia)
#mvn compile
#mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.lucene.index.external.utils.TMFWikiDumpCleaner" -Dexec.args=$INDEX_CONFIG_FILE

# extract valid URIs, synonyms and surface forms from DBpedia
#mvn scala:run -Dlauncher=ExtractCandidateMap "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE"

# decode URIs of the extracted files from the 3.9 version of DBpedia (added by Giuseppe Futia)
#mvn compile
#mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.lucene.index.external.utils.TMFUriDecoder" -Dexec.args=$INDEX_CONFIG_FILE

# now we collect parts of Wikipedia dump where DBpedia resources occur and output those occurrences as Tab-Separated-Values
#echo -e "Parsing Wikipedia dump to extract occurrences...\n"
#mvn scala:run -Dlauncher=ExtractOccsFromWikipedia "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/occs.tsv"

# (recommended) sorting the occurrences by URI will speed up context merging during indexing
#echo -e "Sorting occurrences to speed up indexing...\n"
#sort -t$'\t' -k2 $DBPEDIA_WORKSPACE/output/occs.tsv >$DBPEDIA_WORKSPACE/output/occs.uriSorted.tsv

#set -e
# create a lucene index out of the occurrences
#echo -e "Creating a context index from occs.tsv...\n"
#mvn scala:run -Dlauncher=IndexMergedOccurrences "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/occs.uriSorted.tsv"
# NOTE: if you get an out of memory error from the command above, try editing ../index/pom.xml with correct jvmArg and file arguments, then run:
#mvn scala:run -Dlauncher=IndexMergedOccurrences "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/occs.uriSorted.tsv"

# (optional) make a backup copy of the index before you lose all the time you've put into this
#echo -e "Make a backup copy of the index..."
#cp -R $DBPEDIA_WORKSPACE/output/index $DBPEDIA_WORKSPACE/output/index-backup

# add entity types to index
#echo -e "Adding Types to index... \n"
#mvn scala:run -Dlauncher=AddTypesToIndex "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/index"

# add titles to index (added by Federico Cairo)
#echo -e "Adding Wikipedia Titles to index... \n"
#mvn scala:run -Dlauncher=AddTitlesToIndex "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/index-withTypes"

# add images to index (added by Federico Cairo)
#echo -e "Adding Images to index... \n"
#mvn scala:run -Dlauncher=AddImagesToIndex "-DjavaOpts.Xmx=$JAVA_XMX" "-DaddArgs=$INDEX_CONFIG_FILE|$DBPEDIA_WORKSPACE/output/index-withTypesTitles"

# create the Knowledge Base Index of TellMeFirst (added by Giuseppe Futia)
#echo -e "Create the Knowledge Base Index of TellMeFirst... \n"
mvn compile
mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.lucene.index.external.TMFKnowledgeBaseBuilder" -Dexec.args=$INDEX_CONFIG_FILE

# create the Residual Knowledge Base Index of TellMeFirst (added by Giuseppe Futia)
echo -e "Create the Residual Knowledge Base Index of TellMeFirst... \n"
mvn compile
mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.lucene.index.external.TMFResidualKnowledgeBaseBuilder" -Dexec.args=$INDEX_CONFIG_FILE

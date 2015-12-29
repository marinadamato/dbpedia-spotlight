# This bash script enables services to get new entities for the TellMeFirst domain index
# Each service is wrapped by the Fusepool Transformer in order to standardize each service

# Pull up domain services
export INDEX_CONFIG_FILE=../conf/indexing.tmf.domain.en.properties
cd ../domain-services
mvn compile
mvn exec:java -e -Dexec.mainClass="org.dbpedia.spotlight.LDRService" -Dexec.args="$INDEX_CONFIG_FILE"
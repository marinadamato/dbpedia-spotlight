# This bash script enables services to get new entities for the TellMeFirst domain index
# Each service is wrapped by the Fusepool Transformer in order to standardize each service

# Pull up domain services
mvn compile
mvn exec:java -e -Dexec.mainClass="eu.fusepool.p3.transformer.LDRService" -Dexec.args="$INDEX_CONFIG_FILE"
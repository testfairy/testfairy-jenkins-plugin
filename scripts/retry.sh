#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HPI_OUTPUT_FILENAME="$(pwd)/scripts/TestFairy.hpi"

mvn clean
mvn install
ls -a target/TestFairy.hpi
cp target/TestFairy.hpi "${HPI_OUTPUT_FILENAME}"

cd scripts
bash ./test_jenkins_plugin.sh ${HPI_OUTPUT_FILENAME}
exitcode=$?
cd ..
exit $exitcode
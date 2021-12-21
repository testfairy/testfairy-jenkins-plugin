#!/bin/bash

set -x

apt-get update
apt-get install -y maven
apt-get install -y openjdk-8-jdk

export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-8-openjdk-amd64}
export HPI_OUTPUT_FILENAME="$(pwd)/scripts/TestFairy.hpi"
export TF_API_KEY=$TF_API_KEY

mvn clean
mvn install
ls -a target/TestFairy.hpi
cp target/TestFairy.hpi "${HPI_OUTPUT_FILENAME}"

cd scripts
bash ./install_jenkins.sh
bash ./test_jenkins_plugin.sh ${HPI_OUTPUT_FILENAME}
exitcode=$?
cd ..
exit $exitcode
#!/bin/bash

export TF_API_KEY=$TF_API_KEY

if [ ! -f $1 ]; then
	echo "TestFairy plugin hpl file not found at path [$1]."
	exit 2
fi

echo "Installing $1"
ls ~/.jenkins/war/WEB-INF/jenkins-cli.jar
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ install-plugin $1
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ restart
sleep 15

# Fail early
set -e

# Test 1
bash upload_test/upload_test.sh

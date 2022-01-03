#!/bin/bash

export TF_API_KEY=$TF_API_KEY
export TEMP_JENKINS_ADMIN_PASS=`cat /root/.jenkins/secrets/initialAdminPassword`

if [ ! -f $1 ]; then
	echo "TestFairy plugin hpl file not found at path [$1]."
	exit 2
fi

# Fail early
set -e

# Install plugin
echo "Installing $1"
ls jenkins-cli.jar
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS install-plugin file://`realpath $1`
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS restart
sleep 15

# Test 1
bash upload_test/upload_test.sh

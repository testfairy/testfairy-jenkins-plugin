#!/bin/bash

export JENKINS_TEST_CONFIG="$(pwd)/upload_test/UploadTest.xml"

if [ ! -f $JENKINS_TEST_CONFIG ]; then
	echo "Jenkins test config file not found at path [$JENKINS_TEST_CONFIG]."
	exit 2
fi

sed -i "s/REPLACE_ME/$TF_API_KEY/g" $JENKINS_TEST_CONFIG || echo "Api key already set"

java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS list-plugins
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS delete-job UploadTest && echo "Deleting previous job..."
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS create-job UploadTest < $JENKINS_TEST_CONFIG
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS list-jobs

echo "Testing"
java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS build UploadTest

while true; do
	java -jar jenkins-cli.jar -s http://localhost:8080/ -auth admin:$TEMP_JENKINS_ADMIN_PASS console UploadTest > /tmp/console.log
	sleep 2
	echo console...
	cat /tmp/console.log
	grep "Finished: FAILURE" /tmp/console.log
	if [ $? == 0 ]; then
		echo Finished: FAILURE
		cat /tmp/console.log
		exit 1;
	fi

	echo check SUCCESS
	grep "Finished: SUCCESS" /tmp/console.log
	if [ $? == 0 ]; then
		exit 0;
	fi
done
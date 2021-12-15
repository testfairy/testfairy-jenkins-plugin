#!/bin/bash

export JENKINS_TEST_CONFIG="$(pwd)/upload_test/UploadTest.xml"

if [ ! -f $JENKINS_TEST_CONFIG ]; then
	echo "Jenkins test config file not found at path [$JENKINS_TEST_CONFIG]."
	exit 2
fi

sed "s/REPLACE_ME/$TF_API_KEY/g" $JENKINS_TEST_CONFIG || echo "Api key already set"

java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-plugins
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ delete-job UploadTest && echo "Deleting previous job..."
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ create-job UploadTest < $JENKINS_TEST_CONFIG
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-jobs

echo "Testing"
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ build UploadTest

while true; do
	java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ console UploadTest > /tmp/console.log
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
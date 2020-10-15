#!/bin/bash

if [ ! -f $1 ]; then
	echo "TestFairy plugin hpl file not found at path [$1]."
	exit 2
fi

if [ ! -f $2 ]; then
	echo "Jenkins test config file not found at path [$2]."
	exit 2
fi

echo "Installing $1"
ls ~/.jenkins/war/WEB-INF/jenkins-cli.jar
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ install-plugin $1
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ restart
sleep 15
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-plugins
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ create-job JenkinsTest < $2
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-jobs

echo "Testing"
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ build JenkinsTest

while true; do
	java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ console JenkinsTest > /tmp/console.log
	sleep 2
	echo console...
	cat /tmp/console.log
	grep "Finished: FAILURE" /tmp/console.log
	if [ $? == 0 ]; then
		echo Finished: FAILURE
		cat /tmp/console.log
		exit -1;
	fi

	echo check SUCCESS
	grep "Finished: SUCCESS" /tmp/console.log
	if [ $? == 0 ]; then
		exit 0;
	fi
done

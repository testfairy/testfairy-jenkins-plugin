#!/bin/bash

build() {
    echo start build
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
            echo $1 "FAILED!!!"
            exit -1;
        fi

        echo check SUCCESS
        grep "Finished: SUCCESS" /tmp/console.log
        if [ $? == 0 ]; then
            echo $1 "WORK !!!"
            break;
        fi
    done
}


buildTheHpi() {

}

installJenkins() {

}

pluginPath=/home/travis/build/testfairy/testfairy-jenkins-plugin

installJenkins
buildTheHpi

ls ~/.jenkins/war/WEB-INF/jenkins-cli.jar
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ install-plugin $pluginPath/test/TestFairy.hpi
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ restart
sleep 15
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-plugins
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ create-job JenkinsTest < $pluginPath/test/JenkinsTest.xml
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-jobs

cd $pluginPath/test
build

echo "Plugin $pluginPath/target/TestFairy.hpi passed"

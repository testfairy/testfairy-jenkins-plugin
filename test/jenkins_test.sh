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


mvnInstall() {
    cd /home/travis/build/testfairy/testfairy-jenkins-plugin
    mvn install
    cp target/TestFairy.hpi test/
    cd test

}

installJenkins() {
    cd /home/travis/build/testfairy/testfairy-jenkins-plugin/test
    curl -Lo jenkins.war http://mirrors.jenkins-ci.org/war/latest/jenkins.war
    ls;

    java -jar jenkins.war&
    sleep 45

}

mvnInstall
installJenkins

/home/travis/build/testfairy/testfairy-jenkins-plugin/test

ls ~/.jenkins/war/WEB-INF/jenkins-cli.jar
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ install-plugin TestFairy.hpi
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ restart
sleep 15
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-plugins
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ create-job JenkinsTest < JenkinsTest.xml
java -jar ~/.jenkins/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080/ list-jobs


dname=("CN=common_name" "OU=organizational_unit" "O=organization" "L=locality" "S=state" "C=US")

rm jenkins.keystore
#type=jks, alias=androiddebugkey, storepass= android, keypass=123456789
keytool -genkey -keystore jenkins.keystore -alias androiddebugkey -storepass android -keyalg RSA -keysize 2048 -validity 3650 -dname $dname[*] -keypass 123456789 2>&1;
build "type=jks, alias=androiddebugkey, storepass= android, keypass=123456789"

rm jenkins.keystore
#type=pkcs12, alias=androiddebugkey, storepass= android, keypass=123456789
keytool -genkey -keystore jenkins.keystore -alias androiddebugkey -storepass android -keyalg RSA -keysize 2048 -validity 3650 -dname $dname[*] -keypass 123456789 -storetype pkcs12 2>&1;
build "type=pkcs12, alias=androiddebugkey, storepass= android, keypass=123456789"

rm jenkins.keystore
#type=pkcs12, alias=androiddebugkey, storepass= android
keytool -genkey -keystore jenkins.keystore -alias androiddebugkey -storepass android -keyalg RSA -keysize 2048 -validity 3650 -dname $dname[*] -storetype pkcs12 2>&1;
build "type=pkcs12, alias=androiddebugkey, storepass= android"

#rm jenkins.keystore
##type=pkcs11, alias=androiddebugkey, storepass= android, keypass=123456789
#keytool -genkey -keystore jenkins.keystore -alias androiddebugkey -storepass android -keyalg RSA -keysize 2048 -validity 3650 -dname $dname[*] -keypass 123456789 -storetype pkcs11 2>&1;
#build "type=pkcs11, alias=androiddebugkey, storepass= android, keypass=123456789"

echo Yay the build work!!!

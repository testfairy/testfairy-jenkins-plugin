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
    echo mvnInstall...
    cd $pluginPath
    mvn install
    cp $pluginPath/target/TestFairy.hpi $pluginPath/test/
    cd $pluginPath/test/
    if [ ! -f TestFairy.hpi ]; then
        echo "TestFairy.hpi File not found!, the build probably failed"
        exit 2
    fi


}

installJenkins() {
    echo installJenkins...
    cd $pluginPath/test/
    curl -Lo jenkins.war http://mirrors.jenkins-ci.org/war/latest/jenkins.war
    ls;

    echo run jenkins.war and sleep for 45 sec....
    java -jar jenkins.war&
    sleep 45

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

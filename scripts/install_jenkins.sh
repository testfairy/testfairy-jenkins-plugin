#!/bin/bash

echo "Installing Jenkins..."
curl -Lo jenkins.war https://s3.amazonaws.com/testfairy/static/Jenkins/jenkins_1_956.war
ls;

echo "Running jenkins.war and sleeping for 45 sec...."
java -Dhudson.DNSMultiCast.disabled=true -jar jenkins.war&
sleep 45

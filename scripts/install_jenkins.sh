#!/bin/bash

echo "Installing Jenkins..."
curl -Lo jenkins.war https://get.jenkins.io/war-stable/latest/jenkins.war
ls;

echo "Running jenkins.war and sleeping for 45 sec...."
java -Dhudson.DNSMultiCast.disabled=true -Djenkins.install.runSetupWizard=false -jar jenkins.war&
sleep 45

echo "Installing Jenkins CLI.."
curl -Lo jenkins-cli.jar http://localhost:8080/jnlpJars/jenkins-cli.jar
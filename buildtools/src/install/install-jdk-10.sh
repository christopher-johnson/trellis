#!/bin/bash

# Based on JDK 10 installation script from the junit5 project

set -e

JDK_FEATURE=10
TMP=$(curl -L jdk.java.net/${JDK_FEATURE})              # extract most recent "JDK_BUILD" number from web page source
TMP="${TMP#*Most recent build: jdk-${JDK_FEATURE}+}"    # remove everything before the number
TMP="${TMP%%<*}"                                        # remove everything after the number
JDK_BUILD="$(echo -e "${TMP}" | tr -d '[:space:]')"     # remove all whitespace
JDK_ARCHIVE=openjdk-${JDK_FEATURE}+${JDK_BUILD}_linux-x64_bin.tar.gz

cd ~
wget https://download.java.net/java/jdk${JDK_FEATURE}/archive/${JDK_BUILD}/GPL/${JDK_ARCHIVE}
tar -xzf ${JDK_ARCHIVE}
export JAVA_HOME=~/jdk-${JDK_FEATURE}
export PATH=${JAVA_HOME}/bin:$PATH
cd -
java --version

# Install Amazon's root cacerts
wget https://www.amazontrust.com/repository/AmazonRootCA1.pem
keytool -import -alias amazon1 -storepass changeit -noprompt -keystore ${JAVA_HOME}/lib/security/cacerts -file AmazonRootCA1.pem
wget https://www.amazontrust.com/repository/AmazonRootCA2.pem
keytool -import -alias amazon2 -storepass changeit -noprompt -keystore ${JAVA_HOME}/lib/security/cacerts -file AmazonRootCA2.pem
wget https://www.amazontrust.com/repository/AmazonRootCA3.pem
keytool -import -alias amazon3 -storepass changeit -noprompt -keystore ${JAVA_HOME}/lib/security/cacerts -file AmazonRootCA3.pem
wget https://www.amazontrust.com/repository/AmazonRootCA4.pem
keytool -import -alias amazon4 -storepass changeit -noprompt -keystore ${JAVA_HOME}/lib/security/cacerts -file AmazonRootCA4.pem

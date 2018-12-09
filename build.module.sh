#!/bin/bash

export JAVA_HOME=/home/scmtools/buildkit/jdk-1.8u92/
echo $JAVA_HOME
export PATH=${JAVA_HOME}/bin:${PATH}
echo $PATH

bash ./gradlew $1:output

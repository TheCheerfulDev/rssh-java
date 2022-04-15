#!/usr/bin/env bash

mvn clean package &>/dev/null
cp target/rssh-java.jar /Users/mark/dev/thecheerfuldev/other/lib/rssh-java.jar

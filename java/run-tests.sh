#!/bin/sh
# Compile the Java versions of the data-rep test programs and check that their
# output matches the output of the original COBOL programs.
set -e
cd "$(dirname "$0")"
rm -rf build
mkdir -p build/classes
javac --release 11 -d build/classes $(find src/main/java src/test/java -name '*.java')
cp -r src/test/resources/. build/classes/
java -cp build/classes jp.osscons.datarep.DataRepTest

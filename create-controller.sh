#!/bin/sh

sbt 'g8Scaffold controller'
cp -rp generated-test/* test/
rm -fr generated-test

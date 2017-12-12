#!/bin/sh

sbt 'g8Scaffold model'
cp -rp generated-test/* test/
rm -fr generated-test
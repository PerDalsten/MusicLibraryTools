#/bin/bash

DERBY_DATA=$PWD

JAVA=$JAVA_HOME/bin/java

# Client connect:
# Embedded: CONNECT 'jdbc:derby:musiclibrarydb;create=true';
# Network Client: CONNECT 'jdbc:derby://localhost:1527/musiclibrarydb;create=true';

# To create database from ij: run '../resources/musiclibrary-derby.sql';

$JAVA -Dderby.system.home=$DERBY_DATA -jar $DERBY_HOME/lib/derbyrun.jar ij
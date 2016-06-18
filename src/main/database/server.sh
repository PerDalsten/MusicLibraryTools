#/bin/bash

DERBY_HOME=$JAVA_HOME/db
DERBY_DATA=$PWD
JAVA=$JAVA_HOME/bin/java

$JAVA -Dderby.system.home=$DERBY_DATA -jar $DERBY_HOME/lib/derbyrun.jar server start
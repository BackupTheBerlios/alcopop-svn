#!/bin/sh
CLASSPATH=".:alcopop.jar:lib/mysql-connector-java-3.1.10-bin.jar:lib/JNetStream.jar"
JVM_ARGS="-server -Xms64m -Xmx512m"
JMX_ARGS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8004 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
$JAVA_HOME/bin/java $JVM_ARGS -classpath "$CLASSPATH" $JMX_ARGS com.shelljunkie.alcopop.Main -p $@

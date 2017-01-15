#!/bin/bash
CP=""
#asm
CP=$CP"lib/asm-debug-all-4.1.jar"
#disl
CP=$CP":lib/disl-server.jar"
#analysis specific instrumentation
CP=$CP":output/lib/rv.jar:output/lib/analysis.jar:output/lib/instr.jar"
#jadb
CP=$CP":lib/jadb-1.0.0-SNAPSHOT.jar"
#dex2jar
CP=$CP":lib/dex-reader-2.1-SNAPSHOT.jar:lib/dex-tools-2.1-SNAPSHOT.jar:lib/dex-translator-2.1-SNAPSHOT.jar:lib/dex-reader-api-2.1-SNAPSHOT.jar:lib/dex-ir-2.1-SNAPSHOT.jar"
#dx
CP=$CP":lib/dx.jar"
#android
CP=$CP":lib/framework.jar:lib/core.jar"


echo "Usage: [path_to_config]"
if [ "$#" -eq 0 ]; then
  java -Ddislserver.instrumented="/tmp/debug" -Xmx8g -cp $CP  ch.usi.dag.disldroidserver.DiSLServer
else
  java -Ddislserver.instrumented="/tmp/debug" -Dconfig.path=$1 -Xmx8g -cp $CP ch.usi.dag.disldroidserver.DiSLServer
fi


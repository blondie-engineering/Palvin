#!/bin/bash
export KAFKA=localhost:9092
export TOPIC=adData

buildProject="sbt build"
runProject="sbt run"

eval $buildProject
eval $runProject



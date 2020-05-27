#!/bin/bash
export KAFKA=localhost:9092
export TOPIC=adData
export IS_LOCAL=false
export AWS_ACCESS_KEY_ID=xxx
export AWS_SECRET_ACCESS_KEY=xxx
export AWS_REGION=xxx
buildProject="sbt build"
runProject="sbt run"

eval $buildProject
eval $runProject



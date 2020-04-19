#!/bin/bash
PATH_TO_KAFKA=.
iterations=$1
${PATH_TO_KAFKA}/bin/zookeeper-server-start.sh config/zookeeper.properties > /dev/null 2>&1 &
sleep 5
${PATH_TO_KAFKA}/bin/kafka-server-start.sh config/server.properties > /dev/null 2>&1 &
sleep 5
while [ $iterations -ge 0 ]
do
   echo Creating AdData - iteration: $iterations
   sleep $2
   python3 generateAdData.py $3 $4
   ${PATH_TO_KAFKA}/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic adData < ./transactions.txt
   ((iterations=iterations-1))
done

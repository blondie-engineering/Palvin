#!/bin/bash

iterations=$1
while [ $iterations -ge 0 ]
do
   echo Creating AdData - iteration: $iterations
   sleep $2
   python3 generateAdData.py $3 $4
   bin/kafka-console-producer.sh --broker-list localhost:9092 --topic adData < ./transactions.txt
   ((iterations=iterations-1))
done
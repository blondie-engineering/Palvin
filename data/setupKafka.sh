#!/bin/bash
#download kafka to data directory

#tar -xzf kafka_2.11-2.3.0.tgz
#cd kafka_2.11-2.3.0
PATH_TO_KAFKA=.
${PATH_TO_KAFKA}/bin/zookeeper-server-start.sh config/zookeeper.properties &
${PATH_TOKAFKA}/bin/kafka-server-start.sh config/server.properties &
python3 generateAdData.py
sleep 5 
${PATH_TO_KAFKA}/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic adData < ./transactions.txt

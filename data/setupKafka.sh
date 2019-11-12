#!/bin/bash
#download kafka to data directory
tar -xzf kafka_2.11-2.3.0.tgz
cd kafka_2.11-2.3.0
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
python3 generateAdData.py
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic adData < ./transactions.txt
export KAFKA_URL=localhost:9092
export TOPIC=adData


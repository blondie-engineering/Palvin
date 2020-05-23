#!/bin/bash
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic adData < ./transactions.txt 

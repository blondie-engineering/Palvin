# Palvin
[![Build Status](https://travis-ci.org/blondie-engineering/Palvin.svg?branch=master)](https://travis-ci.org/blondie-engineering/Palvin)
## Real time Ad Data processing engine
### Guns used: Scala, Spark, Python, Shell, Kafka

![alt text](https://github.com/blondie-engineering/Palvin/blob/master/media/palvin.jpg)

### How to run
```
cd data
./spotify.sh
python generateAdData.py $1 $2 $3 $4
./shootData.sh

cd ../engine
./run.sh

```

package eu.walczakpawel

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder

object Main {

  def main(args: Array[String]) {

    val ssc = new StreamingContext("local[*]", "AdStream", Seconds(1))
    val kafkaParams = Map("metadata.broker.list" -> System.getenv("KAFKA_URL"))
    val topics = List(System.getenv("TOPIC")).toSet
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics)
      .map(_._2)
      .map(_.split(" "))
        .map(l => (l(0), l(1)))
        .groupByKey()
        .mapValues(_.map(_.toInt).sum)


    lines.print

    ssc.start()
    ssc.awaitTermination()
  }
}


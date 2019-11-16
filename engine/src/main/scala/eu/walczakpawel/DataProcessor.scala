package eu.walczakpawel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder
import java.util.Calendar

import eu.walczakpawel.db.Connector
import eu.walczakpawel.model.Campaign

class DataProcessor {

  val dbConnector = new Connector()

  def processDataFromKafka(): Unit = {
    val calendar = Calendar.getInstance()
    val ssc = new StreamingContext("local[*]", "AdStream", Seconds(1))
    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")
    val topics = List("adData").toSet
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics)
      .map(_._2)
      .map(_.split(" "))
        .map(l => (l(0), l(1)))
        .mapValues((_, calendar.getTime().toString))

    lines.map(l => new Campaign(l._1, l._2._1.toInt, l._2._2)).foreachRDD(c => dbConnector.loadCampaigns(c.collect().head))

    ssc.start()
    ssc.awaitTermination()
  }

}

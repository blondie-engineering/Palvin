package eu.walczakpawel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder
import java.util.Calendar

import eu.walczakpawel.db.Connector
import eu.walczakpawel.model.Campaign

object DataProcessor {


  def processDataFromKafka(): Unit = {
    val calendar = Calendar.getInstance()
    val ssc = new StreamingContext("local[*]", "AdStream", Seconds(1))
    val kafkaParams = Map("metadata.broker.list" -> sys.env("KAFKA"))
    val topics = List(sys.env("TOPIC")).toSet
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics)
      .map(_._2)
      .map(_.split(" "))
        .map(l => (l(0), l(1).toInt))
        .foreachRDD(c => {
          val campaigns = c.collect().toList.groupBy(_._1).mapValues(v => v.map(_._2).sum).map(cp => Campaign(cp._1, cp._2)).toList

          if(!c.isEmpty()) Connector.loadCampaigns(campaigns)
        })

    ssc.start()
    ssc.awaitTermination()
  }

}

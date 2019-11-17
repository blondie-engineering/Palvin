package eu.walczakpawel

import eu.walczakpawel.db.Connector

object Main {

  def main(args: Array[String]) {
     new Connector().scanTable()
    //val dp = new DataProcessor()
    //dp.processDataFromKafka()
  }
}


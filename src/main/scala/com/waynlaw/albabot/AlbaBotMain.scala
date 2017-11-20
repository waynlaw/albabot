package com.waynlaw.albabot

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.strategist.{RealWorld, Strategist}
import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.runner.Runner
import com.waynlaw.albabot.util.{BithumbApi, HtmlUtil}

object AlbaBotMain extends LazyLogging{
  def main(args: Array[String]) {
    Console println Configure.load()

    val realWorld = new RealWorld()
    val strategist = new Strategist()
    val runner = new Runner(
      StrategistModel(),
      strategist.compute,
      realWorld,
      realWorld
    )
    //    runner.start()

    //    Console println BithumbApi.getTicker("BCH")
    //    Console println BithumbApi.getOrderbook("BCH")
    //    Console println BithumbApi.getRecentTransactions("BCH")

    //    Console println BithumbApi.tickerInfo("BTC")

//    logger.debug("here")
    Console println BithumbApi.place("QTUM", "0.1", "15850", "bid")
//     orderId: String, `type`: String, count: String,  after: String
//    Console println BithumbApi.orders("QTUM", "1511180986926412", "bid", "100", "1511211000")
//    Console println BithumbApi.orderDetail("QTUM", "1511179593025365", "bid")
  }
}
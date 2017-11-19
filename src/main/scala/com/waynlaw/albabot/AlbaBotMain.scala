package com.waynlaw.albabot

import com.waynlaw.albabot.strategist.{RealWorld, Strategist}
import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.runner.Runner
import com.waynlaw.albabot.util.{BithumbApi, HtmlUtil}

object AlbaBotMain {
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

  }
}
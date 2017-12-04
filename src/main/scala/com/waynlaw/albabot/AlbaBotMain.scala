package com.waynlaw.albabot

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.runner.{Collector, Runner}
import com.waynlaw.albabot.strategist.{DecisionMaker, RealWorld, Strategist}

object AlbaBotMain extends LazyLogging{
  def main(args: Array[String]) {
    Console println Configure.load()

    val coinType = CoinType.BTC
    val realWorld = new RealWorld(coinType)
    val decisionMaker = new DecisionMaker(coinType)
    val strategist = new Strategist(decisionMaker, 1000)
    val runner = new Runner(
      StrategistModel(),
      strategist.compute,
      realWorld,
      realWorld
    )
    runner.start()

    val collector = new Collector()
    collector.run()

  }
}
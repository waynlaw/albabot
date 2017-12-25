package com.waynlaw.albabot

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.runner.{Collector, Runner}
import com.waynlaw.albabot.strategist.{DecisionMaker, RealWorld, Strategist}

object AlbaBotMain extends LazyLogging{
  def main(args: Array[String]) {
    Console println Configure.load()

    val realWorld = new RealWorld(CoinType.BTC)
    val decisionMaker = new DecisionMaker()
    val strategist = new Strategist(-3)
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

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
    val strategist = new Strategist(coinUnitExp = coinType.minUnitExp)
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

package com.waynlaw.albabot

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.strategist.{DecisionMaker, RealWorld, Strategist}
import com.waynlaw.albabot.strategist.model.{State, StrategistModel}
import com.waynlaw.albabot.strategist.runner.Runner

object AlbaBotMain extends LazyLogging{
  def main(args: Array[String]) {
    Console println Configure.load()

    val targetCoinType = CoinType.BTC

    val realWorld = new RealWorld(targetCoinType)
    val decisionMaker = new DecisionMaker(coinUnitExp = -4)
    val strategist = new Strategist(decisionMaker, 1000)
    val runner = new Runner(
      StrategistModel(),
      strategist.compute,
      realWorld,
      realWorld
    )
    runner.start()
  }
}
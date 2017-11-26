package com.waynlaw.albabot

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.strategist.{DecisionMaker, RealWorld, Strategist}
import com.waynlaw.albabot.strategist.model.{State, StrategistModel}
import com.waynlaw.albabot.strategist.runner.Runner

object AlbaBotMain extends LazyLogging{
  def main(args: Array[String]) {
    Console println Configure.load()

    val realWorld = new RealWorld()
    val decisionMaker = new DecisionMaker(coinUnitExp = -4)
    val strategist = new Strategist(decisionMaker, 1000)
    val runner = new Runner(
      StrategistModel(
        state = State.WaitingCurrencyInfo(),
        krw = 20000
      ),
      strategist.compute,
      realWorld,
      realWorld
    )
    runner.start()
  }
}
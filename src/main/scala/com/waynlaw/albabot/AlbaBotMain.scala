package com.waynlaw.albabot

import com.waynlaw.albabot.strategist.{RealWorld, Strategist}
import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.strategist.runner.Runner
import com.waynlaw.albabot.util.BithumbApi

object AlbaBotMain{
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
    runner.start()

    Console println BithumbApi.getLastInfo("BTC")
  }
}
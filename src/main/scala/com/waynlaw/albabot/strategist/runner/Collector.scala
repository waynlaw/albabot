package com.waynlaw.albabot.strategist.runner

import com.typesafe.config.ConfigFactory
import com.waynlaw.albabot.model.TickerAll
import com.waynlaw.albabot.storage.Storage
import com.waynlaw.albabot.util.BithumbApi

/**
  *
  * @author: Lawrence
  * @since: 2017. 12. 4.
  * @note:
  */
class Collector extends Thread{

  val tick = ConfigFactory.load().getLong("collector.tick")

  val storage = Storage

  override def run(): Unit = {
    while(true){
        val all: TickerAll = BithumbApi.tickerAll
        storage.set(all)
        Thread.sleep(tick)
    }
  }
}

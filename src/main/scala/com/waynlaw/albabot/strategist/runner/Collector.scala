package com.waynlaw.albabot.strategist.runner

import com.typesafe.config.ConfigFactory
import com.waynlaw.albabot.api.database.DatabaseApi
import com.waynlaw.albabot.model.TickerAll
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.storage.Storage
import com.waynlaw.albabot.util.BithumbApi

/**
  *
  * @author: Lawrence
  * @since: 2017. 12. 4.
  * @note:
  */
class Collector extends Thread {

  val tick = ConfigFactory.load().getLong("collector.tick")

  val storage = Storage

  override def run(): Unit = {

    val btcDataList = DatabaseApi.getHistory(CoinType.BTC)
    btcDataList.foreach(storage.set(CoinType.BTC, _))

    while (true) {
      val all: TickerAll = BithumbApi.tickerAll
      storage.set(all)
      Thread.sleep(tick)
    }
  }
}

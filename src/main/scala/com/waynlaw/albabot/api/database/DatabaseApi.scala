package com.waynlaw.albabot.api.database

import com.typesafe.config.ConfigFactory
import com.waynlaw.albabot.api.database.model.DatabaseTickInfo
import com.waynlaw.albabot.model.TickerData
import com.waynlaw.albabot.model.coin.CoinType.Coin
import com.waynlaw.albabot.util.HttpClient
import org.json4s.DefaultFormats

object DatabaseApi {
  implicit val formats = DefaultFormats

  val baseUrl: String = ConfigFactory.load().getString("databaseUrl")
  val client = new HttpClient()

  def getHistory(currency: Coin): List[TickerData] = {
    val api = List(baseUrl, "/bithumb/ticker/BTC/100").mkString("/")
    val tickInfoList = client.get(api).extract[List[DatabaseTickInfo]]
    tickInfoList.map(TickerData.from)
  }
}

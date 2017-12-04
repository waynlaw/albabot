package com.waynlaw.albabot.storage

import java.util.concurrent.ConcurrentHashMap

import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.model.{Ticker, TickerAll, TickerData}
import com.waynlaw.albabot.model.coin.CoinType.Coin

import scala.collection.JavaConverters._
import scala.collection.concurrent._

/**
  *
  * @author: Lawrence
  * @since: 2017. 12. 4.
  * @note:
  */
object Storage {

  private val coinToTickers = new ConcurrentHashMap[Coin, List[TickerData]]()

  def get(): Map[Coin, List[TickerData]] = coinToTickers asScala

  def get(key: String): List[TickerData] = {
    coinToTickers.get(CoinType.valueOf(key))
  }

  def get(coin: Coin): List[TickerData] = coinToTickers.get(coin)


  def set(key: String, value: TickerData): List[TickerData] = {
    this.set(CoinType.valueOf(key), value)
  }

  def set(coin: Coin, value: TickerData): List[TickerData] = {
    val tickers: List[TickerData] = coinToTickers.get(coin)

    if(tickers == null) {
      coinToTickers.put(coin, List(value))
    } else {
      coinToTickers.put(coin, tickers :+ value)
    }

  }

  def set(lists: Map[Coin, TickerData]): List[TickerData] = {
    lists.flatMap(t => this.set(t._1, t._2)).toList
  }

  def set(tickerAll: TickerAll): TickerAll = {
    tickerAll.data.keySet.foreach(k => this.set(k, tickerAll.data.get(k).get))
    tickerAll
  }
}

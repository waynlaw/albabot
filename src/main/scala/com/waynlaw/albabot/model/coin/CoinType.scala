package com.waynlaw.albabot.model.coin

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 20.
  * @note:
  */
object CoinType {
  sealed abstract class Coin( val minUnit: Double,
                              val value: String,
                              val strName: String) {

    def isMinimumUnit(unit: Double): Boolean = {
      if(unit >= this.minUnit) {
        true
      } else {
        false
      }
    }
  }

  /*
  - 1회 최소 수량 (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.01 | ETC: 0.01 | XRP: 21 | BCH: 0.005 | XMR: 0.1 | ZEC: 0.01 | QTUM: 0.1)
   */
  case object ALL extends Coin(0.0d, "ALL", "비트코인")

  case object BTC extends Coin(0.001d, "BTC", "비트코인")

  case object ETH extends Coin(0.01d, "ETH", "이더리움")

  case object DASH extends Coin(0.01d, "DASH", "대시")

  case object LTC extends Coin(0.01d, "LTC", "라이트코인")

  case object ETC extends Coin(0.01d, "ETC", "이더리움 클래식")

  case object XRP extends Coin(21d, "XRP", "리플")

  case object BCH extends Coin(0.005d, "BCH", "비트코인 캐시")

  case object XMR extends Coin(0.1d, "XMR", "모네로")

  case object ZEC extends Coin(0.01d, "ZEC", "제트캐시")

  case object QTUM extends Coin(0.1d, "QTUM", "퀀텀")

  def valueOf(value: String): Coin = {
    value match {
      case "BTC" => CoinType.BTC
      case "ETH" => CoinType.ETH
      case "DASH" => CoinType.DASH
      case "LTC" => CoinType.LTC
      case "ETC" => CoinType.ETC
      case "XRP" => CoinType.XRP
      case "BCH" => CoinType.BCH
      case "XMR" => CoinType.XMR
      case "ZEC" => CoinType.ZEC
      case "QTUM" => CoinType.QTUM
      case _ =>
        CoinType.ALL
    }

  }
}

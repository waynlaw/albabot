package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.util.RealNumber

object Action {
    sealed trait ActionVal

    case object RequestUserBalance extends ActionVal
    case object RequestCurrency extends ActionVal

    case class RequestBuy(amount: RealNumber, price: BigInt, timestamp: BigInt) extends ActionVal
    case class RequestSell(amount: RealNumber, price: BigInt, timestamp: BigInt) extends ActionVal
    case class RequestTradingInfo(tradingId: String, isBuying: Boolean) extends ActionVal
}


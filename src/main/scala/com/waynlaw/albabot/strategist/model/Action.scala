package com.waynlaw.albabot.strategist.model

object Action {
    sealed trait ActionVal

    case object RequestUserBalance extends ActionVal
    case object RequestCurrency extends ActionVal
    case class RequestBuy(amount: BigInt, price: BigInt, timestamp: BigInt) extends ActionVal
    case class RequestSell(amount: BigInt, price: BigInt, timestamp: BigInt) extends ActionVal
}


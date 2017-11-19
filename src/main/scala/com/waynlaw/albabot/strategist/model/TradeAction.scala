package com.waynlaw.albabot.strategist.model

object TradeAction {
    sealed trait TradeActionVal

    case class Buy(amount: BigInt, price: BigInt) extends TradeActionVal
    case class Sell(amount: BigInt, price: BigInt) extends TradeActionVal
}


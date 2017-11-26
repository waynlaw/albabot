package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.util.RealNumber

object TradeAction {
    sealed trait TradeActionVal

    case class Buy(amount: RealNumber, price: BigInt) extends TradeActionVal
    case class Sell(amount: RealNumber, price: BigInt) extends TradeActionVal
}


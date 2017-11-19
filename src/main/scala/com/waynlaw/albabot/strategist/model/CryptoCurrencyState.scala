package com.waynlaw.albabot.strategist.model

object CryptoCurrencyState {
    sealed trait StateVal
    case object Nothing extends StateVal
    case object UnknownPrice extends StateVal
    case class TryToBuy(timestamp: BigInt) extends StateVal
    case class WaitingForBuying(id: String, remainAmount: BigInt) extends StateVal
    case class TryToSell(timestamp: BigInt) extends StateVal
    case class WaitingForSelling(id: String, remainAmount: BigInt) extends StateVal
}

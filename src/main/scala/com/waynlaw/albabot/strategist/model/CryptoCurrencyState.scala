package com.waynlaw.albabot.strategist.model

object CryptoCurrencyState {
    sealed trait StateVal
    case object Nothing extends StateVal
    case object UnknownPrice extends StateVal
    case class TryToBuy(timestamp: BigInt, failedTime: BigInt = 0) extends StateVal
    case class WaitingForBuying(id: String, transactionIds: List[String]) extends StateVal
    case class TryToSell(timestamp: BigInt, failedTime: BigInt = 0) extends StateVal
    case class WaitingForSelling(id: String, transactionIds: List[String]) extends StateVal
}

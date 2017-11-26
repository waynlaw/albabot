package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.util.RealNumber

object Event {
    case class ConfirmOrderInfo(transactionId: String, amount: RealNumber, priceDiff: BigInt, fee: RealNumber)

    sealed trait EventVal
    case object Tick extends EventVal
    case class ReceivePrice(time: BigInt, cryptoCurrency: BigInt) extends EventVal
    case class ReceiveUserBalance(krw: BigInt, cryptoCurrency: RealNumber) extends EventVal
    case class BuyingOrderConfirmed(timestamp: BigInt, id: String) extends EventVal
    case class SellingOrderConfirmed(timestamp: BigInt, id: String) extends EventVal
    case class ReceiveOrderInfo(orderId: String, confirmed: List[ConfirmOrderInfo]) extends EventVal
}


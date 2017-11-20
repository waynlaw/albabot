package com.waynlaw.albabot.strategist.model

object Event {
    sealed trait EventVal
    case object Tick extends EventVal
    case class ReceivePrice(time: BigInt, cryptoCurrency: BigInt) extends EventVal
    case class ReceiveUserBalance(krw: BigInt, cryptoCurrency: BigInt) extends EventVal
    case class BuyingOrderConfirmed(timestamp: BigInt, id: String) extends EventVal
    case class OrderInfo(id: String, traded: String, free: String) extends EventVal
    case class SellingOrderConfirmed(timestamp: BigInt, id: String) extends EventVal
}


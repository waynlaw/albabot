package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.Action.{RequestBuy, RequestCurrency, RequestSell, RequestUserBalance}
import com.waynlaw.albabot.strategist.model.{Action, Event}
import com.waynlaw.albabot.strategist.runner.{Actor, EventSource}
import com.waynlaw.albabot.util.{BithumbApi, StringUtils}

class RealWorld() extends EventSource with Actor {

    var eventQueue: List[Event.EventVal] = List()

    override def fetchEvent: Option[Event.EventVal] = {
        None
    }

    override def run(action: Action.ActionVal): Unit = {
        action match {
            case RequestUserBalance =>
                val balance = BithumbApi.balance("BTC")
                eventQueue = eventQueue :+ Event.ReceiveUserBalance(
                    BigInt(balance.data.availableKrw),
                    StringUtils.BTCStringToBigInt(balance.data.availableBtc)
                )
            case RequestCurrency =>
                val ticker = BithumbApi.ticker("BTC")
                eventQueue = eventQueue :+ Event.ReceivePrice(
                    BigInt(ticker.data.date),
                    (BigInt(ticker.data.buyPrice) + BigInt(ticker.data.sellPrice)) / BigInt(2)
                )
            case RequestBuy(amount, price, timestamp) =>
                val place = BithumbApi.place("BTC", StringUtils.BigIntToBTCString(amount), price.toString, "bid")
                eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
                    timestamp,
                    place.orderId
                )
            case RequestSell(amount, price, timestamp) =>
                val place = BithumbApi.place("BTC", StringUtils.BigIntToBTCString(amount), price.toString, "ask")
                eventQueue = eventQueue :+ Event.SellingOrderConfirmed(
                    timestamp,
                    place.orderId
                )
        }
    }
}

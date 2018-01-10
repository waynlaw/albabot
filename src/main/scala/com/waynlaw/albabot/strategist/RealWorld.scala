package com.waynlaw.albabot.strategist

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.model.{OrderDetailData, TickerData}
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.model.coin.CoinType.Coin
import com.waynlaw.albabot.storage.Storage
import com.waynlaw.albabot.strategist.model.Action._
import com.waynlaw.albabot.strategist.model.{Action, Event}
import com.waynlaw.albabot.strategist.runner.{Actor, EventSource}
import com.waynlaw.albabot.util.{BithumbApi, RealNumber}

class RealWorld(coin: Coin) extends EventSource with Actor with LazyLogging{

    var eventQueue: List[Event.EventVal] = List()

    val storage = Storage

    override def fetchEvent: Option[Event.EventVal] = {
        eventQueue match {
            case x :: xs =>
                eventQueue = xs
                Some(x)
            case _ =>
                None
        }
    }

    override def remainEventNum: Int = {
        eventQueue.length
    }
    /*
     * TODO:
     * 1. Async 로 동작 변경
     * 2. Either의 실패에 대한 처리 추가
     * 3. Buy, Sell을 Place API로 변경
     */
    override def run(action: Action.ActionVal): Unit = {
        action match {
            case RequestUserBalance =>
                val balance = BithumbApi.balance(coin)
                eventQueue = eventQueue :+ Event.ReceiveUserBalance(
                    BigInt(balance.right.get.data.availableKrw),
                    RealNumber(balance.right.get.data.availableBtc)
                )
            case RequestCurrency =>
                try {
                    val tickers: List[TickerData] = storage.get(coin.value)
                    val latestData = tickers.head

                    eventQueue = eventQueue :+ Event.ReceivePrice(
                        BigInt(latestData.date),
                        (BigInt(latestData.buyPrice.toDouble.toInt) + BigInt(latestData.sellPrice.toDouble.toInt)) / BigInt(2)
                    )
                } catch {
                    case _: Throwable =>

                }
            case RequestBuy(amount, price, timestamp) =>
//                val place = BithumbApi.place(coin, amount.toString, price.toString, "bid")
//                eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
//                    timestamp,
//                    place.right.get.orderId
//                )
                try {
                    val result = BithumbApi.orderBuy(coin, amount.toString)
                    result match {
                        case Right(buyResult) =>
                            eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
                                timestamp,
                                buyResult.orderId
                            ) :+ Event.ReceiveOrderInfo(
                                buyResult.orderId,
                                buyResult.data.map(v => Event.ConfirmOrderInfo(
                                    v.contId,
                                    RealNumber(v.units) - RealNumber(v.fee),
                                    price - BigInt(v.price),
                                    RealNumber(v.fee)
                                ))
                            )
                        case _ =>
                            eventQueue = eventQueue :+ Event.OrderFailed(timestamp)
                    }
                } catch {
                    case e: Throwable =>
                        eventQueue = eventQueue :+ Event.OrderFailed(timestamp)
                }

            case RequestSell(amount, price, timestamp) =>
//                val place = BithumbApi.place(coin, amount.toString, price.toString, "ask")
//                eventQueue = eventQueue :+ Event.SellingOrderConfirmed(
//                    timestamp,
//                    place.right.get.orderId
//                )
                try {
                    val result = BithumbApi.orderSell(coin, amount.toString)
                    result match {
                        case Right(sellResult) =>
                            eventQueue = eventQueue :+ Event.SellingOrderConfirmed(
                                timestamp,
                                sellResult.orderId
                            ) :+ Event.ReceiveOrderInfo(
                                sellResult.orderId,
                                sellResult.data.map(v => Event.ConfirmOrderInfo(
                                    v.contId,
                                    RealNumber(v.units),
                                    BigInt(v.price) - price,
                                    RealNumber(v.fee)
                                ))
                            )
                        case _ =>
                            eventQueue = eventQueue :+ Event.OrderFailed(timestamp)
                    }
                } catch {
                    case _: Throwable =>
                        eventQueue = eventQueue :+ Event.OrderFailed(timestamp)
                }

            case RequestTradingInfo(tradingId, isBuying) =>
                val detail = BithumbApi.orderDetail(coin, tradingId, if (isBuying) "bid" else "ask")
                eventQueue = eventQueue :+ Event.ReceiveOrderInfo(
                    tradingId,
                    detail.right.get.data.map {
                        case OrderDetailData(transactionDate, _, _, unitsTraded, price, fee, _) =>
                            Event.ConfirmOrderInfo(
                                transactionDate,
                                RealNumber(unitsTraded),
                                0,
                                RealNumber(fee)
                            )
                    }
                )
            case _ =>
        }
    }
}

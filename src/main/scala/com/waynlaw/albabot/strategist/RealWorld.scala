package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.model.OrderDetailData
import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.model.coin.CoinType.Coin
import com.waynlaw.albabot.strategist.model.Action._
import com.waynlaw.albabot.strategist.model.{Action, Event}
import com.waynlaw.albabot.strategist.runner.{Actor, EventSource}
import com.waynlaw.albabot.util.{BithumbApi, RealNumber}

class RealWorld(coin: Coin) extends EventSource with Actor {

    var eventQueue: List[Event.EventVal] = List()

    override def fetchEvent: Option[Event.EventVal] = {
        eventQueue match {
            case x :: xs =>
                eventQueue = xs
                Some(x)
            case _ =>
                None
        }
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
                val ticker = BithumbApi.ticker(coin)
                println(ticker)
                eventQueue = eventQueue :+ Event.ReceivePrice(
                    BigInt(ticker.data.date),
                    (BigInt(ticker.data.buyPrice.toDouble.toInt) + BigInt(ticker.data.sellPrice.toDouble.toInt)) / BigInt(2)
                )
            case RequestBuy(amount, price, timestamp) =>
//                val place = BithumbApi.place(coin, amount.toString, price.toString, "bid")
//                eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
//                    timestamp,
//                    place.right.get.orderId
//                )
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

                }

            case RequestSell(amount, price, timestamp) =>
//                val place = BithumbApi.place(coin, amount.toString, price.toString, "ask")
//                eventQueue = eventQueue :+ Event.SellingOrderConfirmed(
//                    timestamp,
//                    place.right.get.orderId
//                )
                val result = BithumbApi.orderSell(coin, amount.toString)
                eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
                    timestamp,
                    result.right.get.orderId
                ) :+ Event.ReceiveOrderInfo(
                    result.right.get.orderId,
                    result.right.get.data.map(v => Event.ConfirmOrderInfo(
                        v.contId,
                        RealNumber(v.units),
                        BigInt(v.price) - price,
                        RealNumber(v.fee)
                    ))
                )
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
//            case RequestUserBalance =>
//                val balance = BithumbApi.balance("BTC")
//                eventQueue = eventQueue :+ Event.ReceiveUserBalance(
//                    BigInt(balance.data.availableKrw),
//                    StringUtils.BTCStringToBigInt(balance.data.availableBtc)
//                )
//            case RequestCurrency =>
//                val ticker = BithumbApi.ticker("BTC")
//                eventQueue = eventQueue :+ Event.ReceivePrice(
//                    BigInt(ticker.data.date),
//                    (BigInt(ticker.data.buyPrice) + BigInt(ticker.data.sellPrice)) / BigInt(2)
//                )
//            case RequestBuy(amount, price, timestamp) =>
//                val place = BithumbApi.place("BTC", StringUtils.BigIntToBTCString(amount), price.toString, "bid")
//                eventQueue = eventQueue :+ Event.BuyingOrderConfirmed(
//                    timestamp,
//                    place.orderId
//                )
//            case RequestSell(amount, price, timestamp) =>
//                val place = BithumbApi.place("BTC", StringUtils.BigIntToBTCString(amount), price.toString, "ask")
//                eventQueue = eventQueue :+ Event.SellingOrderConfirmed(
//                    timestamp,
//                    place.orderId
//                )
        }
    }
}

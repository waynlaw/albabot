package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.Event.ConfirmOrderInfo
import com.waynlaw.albabot.strategist.model.TradeAction.Buy
import com.waynlaw.albabot.strategist.model._

/*
 * 소지금은 주문을 하거나, 주문의 결과에 변동이 있는 경우 변경된다.
 */
object KrwUpdater {
    def update(state: StrategistModel, event: Event.EventVal, decisions: DecisionMaker.Decisions): BigInt = {
        (state.state, event) match {
            case (_:State.Init, Event.ReceiveUserBalance(krw, _)) =>
                krw
            case _ =>
                state.krw + changesByConfirmedOrder(state, event) + changesByBuying(state, decisions)
        }
    }

    private def changesByConfirmedOrder(state: StrategistModel, event: Event.EventVal) : BigInt = {
        event match {
            case Event.ReceiveOrderInfo(orderId, confirmed) =>
                state.cryptoCurrency.map(differenceBetweenOrderRequestAndResult(orderId, confirmed)).sum

            case _ =>
                BigInt(0)
        }
    }

    private def differenceBetweenOrderRequestAndResult(orderId: String, confirmed: List[ConfirmOrderInfo])(currencyInfo: CryptoCurrencyInfo): BigInt = {
        currencyInfo match {
            case CryptoCurrencyInfo(_, _, CryptoCurrencyState.WaitingForBuying(buyingId, transactionIds)) if buyingId == orderId =>
                val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                newConfirms.map(v => v.amount * v.priceDiff).sum.toBigInt

            case CryptoCurrencyInfo(_, price, CryptoCurrencyState.WaitingForSelling(sellingId, transactionIds)) if sellingId == orderId =>
                val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                newConfirms.map(v => v.amount * (price + v.priceDiff) - v.fee).sum.toBigInt

            case _ =>
                BigInt(0)
        }
    }

    private def changesByBuying(state: StrategistModel, decisions: DecisionMaker.Decisions) = {
        decisions.tradeAction match {
            case Some(Buy(amount, price)) =>
                -(amount * price).toBigInt
            case _ =>
                BigInt(0)
        }
    }
}

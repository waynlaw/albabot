package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.RealNumber
import com.waynlaw.albabot.util.RealNumber.RealNumberIsNumeric

class Strategist(decisionMaker: DecisionMaker, krwUnit: BigInt = 1) {

    def compute(lastState: StrategistModel, event: Event.EventVal, timestamp: BigInt): (StrategistModel, List[Action.ActionVal]) = {
        val decisions = decisionMaker.Decisions(lastState, event, timestamp, krwUnit)

        val nextState = StrategistModel(
            lastState.state.update(lastState, event, timestamp),
            krw(lastState, event, timestamp, decisions),
            cryptoCurrency(lastState, event, timestamp, decisions),
            lastState.currencyRequester.update(event, timestamp),
            history(lastState, event, timestamp, decisions)
        )
        (nextState, actionList(nextState, event, timestamp, decisions))
    }

    def krw(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: decisionMaker.Decisions): BigInt = {
        (state.state, event) match {
            case (_:State.Init, Event.ReceiveUserBalance(krw, _)) =>
                krw
            case (_, Event.ReceiveOrderInfo(orderId, confirmed)) => {
                val moneyDiff: BigInt = state.cryptoCurrency.map {
                    case CryptoCurrencyInfo(_, _, CryptoCurrencyState.WaitingForBuying(buyingId, transactionIds)) if buyingId == orderId =>
                        val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                        newConfirms.map(v => v.amount * v.priceDiff).sum.toBigInt

                    case CryptoCurrencyInfo(_, price, CryptoCurrencyState.WaitingForSelling(sellingId, transactionIds)) if sellingId == orderId =>
                        val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                        newConfirms.map(v => v.amount * (price + v.priceDiff) - v.fee).sum.toBigInt

                    case _ =>
                        BigInt(0)
                }.sum
                state.krw + moneyDiff
            }
            case _ =>
                decisions.tradeAction match {
                    case Some(Buy(amount, price)) =>
                        state.krw - (amount * price).toBigInt
                    case _ =>
                        state.krw
                }
        }
    }

    def cryptoCurrency(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: decisionMaker.Decisions): List[CryptoCurrencyInfo] = {
        val updatedInfo = event match {
            case Event.ReceiveUserBalance(_, cryptoCurrency) =>
                List(CryptoCurrencyInfo(cryptoCurrency, 0, CryptoCurrencyState.UnknownPrice))
            case Event.ReceivePrice(_, cryptoCurrency) =>
                state.cryptoCurrency.map { x =>
                    x.state match {
                        case CryptoCurrencyState.UnknownPrice =>
                            x.copy(price = cryptoCurrency, state = CryptoCurrencyState.Nothing)
                        case _ =>
                            x
                    }
                }
            case Event.BuyingOrderConfirmed(orderTimestamp, id) =>
                state.cryptoCurrency.map { x =>
                    x.state match {
                        case CryptoCurrencyState.TryToBuy(buyTimestamp) if buyTimestamp == orderTimestamp =>
                            x.copy(state = CryptoCurrencyState.WaitingForBuying(id, Nil))
                        case _ =>
                            x
                    }
                }
            case Event.SellingOrderConfirmed(orderTimestamp, id) =>
                state.cryptoCurrency.map { x =>
                    x.state match {
                        case CryptoCurrencyState.TryToSell(sellingTimestamp) if sellingTimestamp == orderTimestamp =>
                            x.copy(state = CryptoCurrencyState.WaitingForSelling(id, Nil))
                        case _ =>
                            x
                    }
                }

            case Event.ReceiveOrderInfo(orderId, confirmed) => {
                state.cryptoCurrency.flatMap { x =>
                    x match {
                        case CryptoCurrencyInfo(amount, price, state @ CryptoCurrencyState.WaitingForBuying(buyingId, transactionIds)) if buyingId == orderId =>
                            val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                            val reduceAmount = newConfirms.map(v => v.amount + v.fee).sum
                            val sumOfAmount = newConfirms.map(_.amount).sum
                            val appliedTransactionIds = newConfirms.map(_.transactionId) ::: transactionIds
                            val remainAmount = amount - reduceAmount

                            (x.copy(amount = remainAmount, state = state.copy(transactionIds = appliedTransactionIds)) ::
                                CryptoCurrencyInfo(sumOfAmount, price, CryptoCurrencyState.Nothing) ::
                                Nil
                            ).filter(RealNumber(0) < _.amount)
                        case CryptoCurrencyInfo(amount, price, state @ CryptoCurrencyState.WaitingForSelling(sellingId, transactionIds)) if sellingId == orderId =>
                            val newConfirms = confirmed.filter(v => !transactionIds.contains(v.transactionId))
                            val sumOfAmount = newConfirms.map(_.amount).sum
                            val appliedTransactionIds = newConfirms.map(_.transactionId) ::: transactionIds
                            val remainAmount = amount - sumOfAmount

                            (x.copy(amount = remainAmount, state = state.copy(transactionIds = appliedTransactionIds)) :: Nil).filter(RealNumber(0) < _.amount)
                        case _ =>
                            x :: Nil
                    }
                }
            }
            case _ =>
                state.cryptoCurrency
        }
        val flattenInfo: List[CryptoCurrencyInfo] = updatedInfo.groupBy(x => (x.price, x.state))
            .mapValues(x => x.head.copy(x.map(_.amount).sum))
            .values
            .toList

        decisions.tradeAction match {
            case Some(Buy(amount, price)) =>
                CryptoCurrencyInfo(amount, price, CryptoCurrencyState.TryToBuy(timestamp)) :: flattenInfo
            case Some(Sell(amount, price)) =>
                val remainInfo = flattenInfo.sortBy(_.price)
                val sellingItemRemoved = ((amount, List[CryptoCurrencyInfo]()) /: remainInfo) { case((remainAmount, acc), x) =>
                    if (RealNumber(0) == remainAmount) {
                        (RealNumber(0), acc :+ x)
                    } else if (remainAmount < x.amount) {
                        (RealNumber(0), acc :+ x.copy(amount = x.amount - remainAmount))
                    } else {
                        (remainAmount - x.amount, acc)
                    }
                }
                CryptoCurrencyInfo(amount, price, CryptoCurrencyState.TryToSell(timestamp)) :: sellingItemRemoved._2
            case _ =>
                flattenInfo
        }
    }

    def history(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: decisionMaker.Decisions): Array[CurrencyInfo] = {
        event match {
            case Event.ReceivePrice(time, cryptoCurrency) =>
                val newHistory = (state.history :+ CurrencyInfo(cryptoCurrency, time))
                    .filter(x => timestamp - x.timestamp <= Strategist.HISTORY_KEEP_DURATION_MS)
                    .sortBy(_.timestamp)
                newHistory
            case _ =>
                state.history
        }
    }

    def actionList(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: decisionMaker.Decisions): List[Action.ActionVal] = {
        val requestBalance = state.state match {
            case State.Init(balanceRequester) if balanceRequester.willRequestUserBalance =>
                List(Action.RequestUserBalance)
            case _ =>
                Nil
        }

        val requestCurrency = if (state.currencyRequester.willRequestUserCurrency) {
            List(Action.RequestCurrency)
        } else {
            Nil
        }

        val requestTrade = decisions.tradeAction match {
            case Some(Buy(amount, price)) =>
                List(Action.RequestBuy(amount, price, timestamp))
            case Some(Sell(amount, price)) =>
                List(Action.RequestSell(amount, price, timestamp))
            case _ =>
                Nil
        }

        val requestTradingInfo = state.cryptoCurrency.flatMap(x => x.state match {
            case CryptoCurrencyState.WaitingForBuying(id, _) =>
                List(Action.RequestTradingInfo(id, isBuying = true))
            case CryptoCurrencyState.WaitingForSelling(id, _) =>
                List(Action.RequestTradingInfo(id, isBuying = false))
            case _ =>
                Nil
        })

        requestBalance ::: requestCurrency ::: requestTrade ::: requestTradingInfo
    }
}

object Strategist {
    val HISTORY_KEEP_DURATION_MS: BigInt = 120 * 1000
    val HISTORY_MINIMUM_FOR_TRADING: BigInt = 60
}

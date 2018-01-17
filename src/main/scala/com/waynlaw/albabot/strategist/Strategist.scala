package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.CryptoCurrencyState.Nothing
import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.RealNumber
import com.waynlaw.albabot.util.RealNumber.RealNumberIsNumeric

class Strategist(krwUnit: BigInt = 1, coinUnitExp: Int = 0) {

    def compute(lastState: StrategistModel, event: Event.EventVal, timestamp: BigInt): (StrategistModel, List[Action.ActionVal]) = {
        val decisions = DecisionMaker.Decisions(lastState, event, timestamp, krwUnit, coinUnitExp)

        val nextState = StrategistModel(
            lastState.state.update(lastState, event, timestamp),
            KrwUpdater.update(lastState, event, decisions),
            cryptoCurrency(lastState, event, timestamp, decisions),
            lastState.currencyRequester.update(event, timestamp),
            HistoryUpdater.update(lastState.history, event, timestamp),
            decisions
        )
        (nextState, ActionUpdater.evaluate(nextState, timestamp, decisions))
    }

    def cryptoCurrency(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: DecisionMaker.Decisions): List[CryptoCurrencyInfo] = {
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
                        case CryptoCurrencyState.TryToBuy(buyTimestamp, _) if buyTimestamp == orderTimestamp =>
                            x.copy(state = CryptoCurrencyState.WaitingForBuying(id, Nil))
                        case _ =>
                            x
                    }
                }

            case Event.SellingOrderConfirmed(orderTimestamp, id) =>
                state.cryptoCurrency.map { x =>
                    x.state match {
                        case CryptoCurrencyState.TryToSell(sellingTimestamp, _) if sellingTimestamp == orderTimestamp =>
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
                            val newInfoList = newConfirms.map(v => CryptoCurrencyInfo(v.amount, price - v.priceDiff, CryptoCurrencyState.Nothing))
                            val appliedTransactionIds = newConfirms.map(_.transactionId) ::: transactionIds
                            val remainAmount = amount - reduceAmount

                            (x.copy(amount = remainAmount, state = state.copy(transactionIds = appliedTransactionIds)) :: newInfoList
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
            case Event.OrderFailed(failedTimeStamp) => {
                state.cryptoCurrency.map {
                    case v@CryptoCurrencyInfo(_, _, CryptoCurrencyState.TryToBuy(buyTime, _)) if buyTime == failedTimeStamp =>
                        v.copy(state = CryptoCurrencyState.TryToBuy(buyTime, timestamp))
                    case v@CryptoCurrencyInfo(_, _, CryptoCurrencyState.TryToSell(sellTime, _)) if sellTime == failedTimeStamp =>
                        v.copy(state = CryptoCurrencyState.TryToSell(sellTime, timestamp))
                    case v =>
                        v
                }
            }
            case _ =>
                state.cryptoCurrency
        }
        val nonMergeableList = updatedInfo.filter(_.state.getClass != CryptoCurrencyState.Nothing.getClass)
        val mergeableList = updatedInfo.filter(_.state.getClass == CryptoCurrencyState.Nothing.getClass)

        val flattenInfo = if (mergeableList.isEmpty) {
            updatedInfo
        } else {
            val amountSum = mergeableList.map(_.amount).sum
            val summedPrice = mergeableList.map(v => v.amount * v.price).sum.divide(amountSum, -1).toBigInt
            CryptoCurrencyInfo(amountSum, summedPrice, Nothing) :: nonMergeableList
        }

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
}

object Strategist {

    val HISTORY_MINIMUM_FOR_TRADING: BigInt = 100
    val ORDER_RETRY_DURATION_MS: BigInt = 500
}

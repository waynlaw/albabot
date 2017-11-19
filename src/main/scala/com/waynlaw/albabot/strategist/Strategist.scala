package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.{MathUtil, StringUtils}

class Strategist(currencyUnit: BigInt = 1, krwUnit: BigInt = 1) {

    def compute(lastState: StrategistModel, event: Event.EventVal, timestamp: BigInt): (StrategistModel, List[Action.ActionVal]) = {
        val decisions = Strategist.Decisions(lastState, event, timestamp, currencyUnit, krwUnit)

        val nextState = StrategistModel(
            state(lastState, event, timestamp, decisions),
            krw(lastState, event, timestamp, decisions),
            cryptoCurrency(lastState, event, timestamp, decisions),
            lastCurrencyRequestTime(lastState, event, timestamp, decisions),
            lastCurrencyUpdateTime(lastState, event, timestamp, decisions),
            lastBalanceRequestTime(lastState, event, timestamp, decisions),
            lastBalanceUpdateTime(lastState, event, timestamp, decisions),
            history(lastState, event, timestamp, decisions)
        )
        (nextState, actionList(lastState, event, timestamp, decisions))
    }

    def state(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): State.StateVal = {
        (state.state, event) match {
            case (State.Init, _: Event.ReceiveUserBalance) =>
                State.WaitingCurrencyInfo
            case (State.WaitingCurrencyInfo, _) if Strategist.HISTORY_MINIMUM_FOR_TRADING <= state.history.length =>
                State.Trading
            case _ =>
                state.state
        }
    }

    def krw(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): BigInt = {
        (state.state, event) match {
            case (State.Init, Event.ReceiveUserBalance(krw, _)) =>
                krw
            case _ =>
                state.krw
        }
    }

    def cryptoCurrency(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): List[CryptoCurrencyInfo] = {
        val updatedInfo = (state.state, event) match {
            case (State.Init, Event.ReceiveUserBalance(_, cryptoCurrency)) =>
                List(CryptoCurrencyInfo(cryptoCurrency, 0, CryptoCurrencyState.UnknownPrice))
            case (_, Event.ReceivePrice(_, cryptoCurrency)) =>
                state.cryptoCurrency.map{x => x.state match {
                    case CryptoCurrencyState.UnknownPrice =>
                        x.copy(price = cryptoCurrency, state = CryptoCurrencyState.Nothing)
                    case _ =>
                        x
                }}
            case (_, Event.BuyingOrderConfirmed(orderTimestamp, id)) =>
                state.cryptoCurrency.map{x => x.state match {
                    case CryptoCurrencyState.TryToBuy(buyTimestamp) if buyTimestamp == orderTimestamp =>
                        x.copy(state = CryptoCurrencyState.WaitingForBuying(id, x.amount))
                    case _ =>
                        x
                }}
            case _ =>
                state.cryptoCurrency
        }
        decisions.tradeAction match {
            case Some(Buy(amount, price)) =>
                CryptoCurrencyInfo(amount, price, CryptoCurrencyState.TryToBuy(timestamp)) :: updatedInfo
            case Some(Sell(amount, price)) =>
                CryptoCurrencyInfo(amount, price, CryptoCurrencyState.TryToSell(timestamp)) :: updatedInfo // should fix
            case _ =>
                updatedInfo
        }
    }

    def lastCurrencyRequestTime(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): BigInt = {
        decisions.isRequestCurrency match {
            case true =>
                timestamp
            case _ =>
                state.lastCurrencyRequestTime
        }
    }

    def lastCurrencyUpdateTime(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): BigInt = {
        event match {
            case _: Event.ReceivePrice =>
                timestamp
            case _ =>
                state.lastCurrencyUpdateTime
        }
    }

    def lastBalanceRequestTime(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): BigInt = {
        decisions.isRequestUserBalance match {
            case true =>
                timestamp
            case _ =>
                state.lastBalanceRequestTime
        }
    }

    def lastBalanceUpdateTime(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): BigInt = {
        event match {
            case _: Event.ReceiveUserBalance =>
                timestamp
            case _ =>
                state.lastCurrencyUpdateTime
        }
    }

    def history(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): Array[CurrencyInfo] = {
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

    def actionList(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, decisions: Strategist.Decisions): List[Action.ActionVal] = {
        val requestBalance = if (decisions.isRequestUserBalance) {
            List(Action.RequestUserBalance)
        } else {
            Nil
        }

        val requestCurrency = if (decisions.isRequestCurrency) {
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

        requestBalance ::: requestCurrency ::: requestTrade
    }
}

object Strategist {
    val CURRENCY_UPDATE_DURATION_MS: BigInt = 1000
    val BALANCE_UPDATE_DURATION_MS: BigInt = 1000
    val BALANCE_UPDATE_REQUEST_DURATION_MS: BigInt = 500
    val HISTORY_KEEP_DURATION_MS: BigInt = 120 * 1000
    val HISTORY_MINIMUM_FOR_TRADING: BigInt = 60

    case class Decisions(isRequestCurrency: Boolean, isRequestUserBalance: Boolean, tradeAction: Option[TradeAction.TradeActionVal])

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, currencyUnit: BigInt, krwUnit: BigInt ): Decisions = {
            Decisions(
                shouldRequestCurrency(state, timestamp),
                shouldRequestUserBalance(state, timestamp),
                shouldTrade(state, timestamp, currencyUnit, krwUnit)
            )
        }

        def shouldRequestCurrency(state: StrategistModel, timestamp: BigInt): Boolean = {
            Strategist.CURRENCY_UPDATE_DURATION_MS < timestamp - state.lastCurrencyUpdateTime
        }

        def shouldRequestUserBalance(state: StrategistModel, timestamp: BigInt): Boolean = {
            state.state == State.Init &&
                Strategist.BALANCE_UPDATE_DURATION_MS < timestamp - state.lastBalanceUpdateTime &&
                Strategist.BALANCE_UPDATE_REQUEST_DURATION_MS < timestamp - state.lastBalanceRequestTime
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, currencyUnit: BigInt, krwUnit: BigInt): Option[TradeAction.TradeActionVal] = {
            val historyAngle = MathUtil.computeAngle(MathUtil.removeNoise(state.history).map(x => x.copy(timestamp = x.timestamp / 100000)))
            val isDecreasing = 0 > historyAngle
            val lastPrice = state.history.lastOption.map(_.price / krwUnit * krwUnit).getOrElse(BigInt(1))
            val buyableAmount = state.krw * StringUtils.SATOSHI_UNIT / lastPrice / currencyUnit * currencyUnit

            state.state match {
                case State.Init | State.WaitingCurrencyInfo =>
                    None
                case _ if state.history.length < Strategist.HISTORY_MINIMUM_FOR_TRADING =>
                    None
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToBuy]) =>
                    None
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToSell]) =>
                    None
                case _ if !isDecreasing && 0 != buyableAmount =>
                    Some(Buy(buyableAmount, lastPrice))
                case _ =>
                    None
            }
        }
    }
}

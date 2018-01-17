package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}

object ActionUpdater {
    private def requestBalance(state: StrategistModel): List[Action.ActionVal] = {
        state.state match {
            case State.Init(balanceRequester) if balanceRequester.willRequestUserBalance =>
                Action.RequestUserBalance :: Nil
            case _ =>
                Nil
        }
    }

    private def requestCurrency(state: StrategistModel): List[Action.ActionVal] = {
        if (state.currencyRequester.willRequestUserCurrency) {
            Action.RequestCurrency :: Nil
        } else {
            Nil
        }
    }

    private def requestTrade(decisions: DecisionMaker.Decisions, timestamp: BigInt): List[Action.ActionVal] = {
        decisions.tradeAction match {
            case Some(Buy(amount, price)) =>
                Action.RequestBuy(amount, price, timestamp) :: Nil
            case Some(Sell(amount, price)) =>
                Action.RequestSell(amount, price, timestamp) :: Nil
            case _ =>
                Nil
        }
    }

    private def requestTradingInfo(state: StrategistModel): List[Action.ActionVal] = {
        state.cryptoCurrency.flatMap(x => x.state match {
            case CryptoCurrencyState.WaitingForBuying(id, _) =>
                Action.RequestTradingInfo(id, isBuying = true) :: Nil
            case CryptoCurrencyState.WaitingForSelling(id, _) =>
                Action.RequestTradingInfo(id, isBuying = false) :: Nil
            case _ =>
                Nil
        })
    }

    private def retryOrder(state: StrategistModel, timestamp: BigInt): List[Action.ActionVal] = {
        state.cryptoCurrency.flatMap(x => x.state match {
            case CryptoCurrencyState.TryToBuy(orderTimeStamp, errorTime) if 0 != errorTime && Strategist.ORDER_RETRY_DURATION_MS <= timestamp - errorTime =>
                Action.RequestBuy(x.amount, x.price, orderTimeStamp) :: Nil
            case CryptoCurrencyState.TryToSell(orderTimeStamp, errorTime) if 0 != errorTime && Strategist.ORDER_RETRY_DURATION_MS <= timestamp - errorTime =>
                Action.RequestSell(x.amount, x.price, orderTimeStamp) :: Nil
            case _ =>
                Nil
        })
    }

    def evaluate(state: StrategistModel, timestamp: BigInt, decisions: DecisionMaker.Decisions): List[Action.ActionVal] = {
        requestBalance(state) :::
            requestCurrency(state) :::
            requestTrade(decisions, timestamp) :::
            requestTradingInfo(state) :::
            retryOrder(state, timestamp)
    }
}

package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.DecisionMaker.Decisions
import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.{MathUtil, RealNumber}

class DecisionMaker() {


}

object DecisionMaker {
    val MIN_PRICE_DIFF: BigInt = 40000
    val DROP_ALLOW_DIFF: BigInt = 30000
    val BUY_IN_SINGLE_STEP: BigInt = 10 * 10000
    val BUY_TIME_DURATION: BigInt = 3 * 30 * 1000

    case class Decisions(tradeAction: Option[TradeAction.TradeActionVal], isBuyable: Boolean, lastBuyTime: BigInt)

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): Decisions = {
            shouldTrade(state, timestamp, krwUnit, coinUnitExp)
        }

        def isTradeable(state: StrategistModel): Boolean = {
            state.state match {
                case _: State.Init | _: State.WaitingCurrencyInfo =>
                    false
                case _ if state.history.length < Strategist.HISTORY_MINIMUM_FOR_TRADING =>
                    false
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToBuy]) =>
                    false
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToSell]) =>
                    false
                case _ =>
                    true
            }
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): Decisions = {
            val historyAngle = MathUtil.computeAngle(MathUtil.removeNoise(state.history).map(x => x.copy(timestamp = x.timestamp / 1000)))
            val lastPrice = state.history.lastOption.map(_.price / krwUnit * krwUnit).getOrElse(BigInt(1))
            val buyKrw = if (DecisionMaker.BUY_IN_SINGLE_STEP < state.krw) {
                val angleFactor = BigInt((historyAngle / 50.0).toInt max 0)
                DecisionMaker.BUY_IN_SINGLE_STEP * angleFactor
            } else {
                state.krw
            }
            val buyableAmount = RealNumber(buyKrw).divide(lastPrice, coinUnitExp)

            state.state match {
                case _ if !isTradeable(state) =>
                   state.decisions.copy(tradeAction = None, isBuyable = false)
                case _ if 50 <= historyAngle =>
                    if (!state.decisions.isBuyable && RealNumber(0) < buyableAmount && BUY_TIME_DURATION <= timestamp - state.decisions.lastBuyTime) {
                        DecisionMaker.Decisions(Some(Buy(buyableAmount, lastPrice)), isBuyable = true, timestamp)
                    } else {
                        state.decisions.copy(None, isBuyable = false)
                    }
                case _ =>
                    state.decisions.copy(None, isBuyable = false)
            }
        }
    }
}
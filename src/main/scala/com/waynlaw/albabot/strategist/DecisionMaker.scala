package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model._

class DecisionMaker(coinUnitExp: Int = 0) {
    case class Decisions(isRequestCurrency: Boolean, isRequestUserBalance: Boolean, tradeAction: Option[TradeAction.TradeActionVal])

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt): Decisions = {
            Decisions(
                shouldRequestCurrency(state, timestamp),
                shouldRequestUserBalance(state, timestamp),
                shouldTrade(state, timestamp, krwUnit)
            )
        }

        def shouldRequestCurrency(state: StrategistModel, timestamp: BigInt): Boolean = {
            DecisionMaker.CURRENCY_UPDATE_DURATION_MS < timestamp - state.lastCurrencyUpdateTime
        }

        def shouldRequestUserBalance(state: StrategistModel, timestamp: BigInt): Boolean = {
            state.state == State.Init &&
                DecisionMaker.BALANCE_UPDATE_DURATION_MS < timestamp - state.lastBalanceUpdateTime &&
                DecisionMaker.BALANCE_UPDATE_REQUEST_DURATION_MS < timestamp - state.lastBalanceRequestTime
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt): Option[TradeAction.TradeActionVal] = {
            None
        }
    }
}

object DecisionMaker {
    val CURRENCY_UPDATE_DURATION_MS: BigInt = 1000
    val BALANCE_UPDATE_DURATION_MS: BigInt = 1000
    val BALANCE_UPDATE_REQUEST_DURATION_MS: BigInt = 500
    val MIN_PRICE_DIFF: BigInt = 20000
}
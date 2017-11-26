package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model._

class DecisionMaker(coinUnitExp: Int = 0) {
    case class Decisions(isRequestCurrency: Boolean, tradeAction: Option[TradeAction.TradeActionVal])

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt): Decisions = {
            Decisions(
                shouldRequestCurrency(state, timestamp),
                shouldTrade(state, timestamp, krwUnit)
            )
        }

        def shouldRequestCurrency(state: StrategistModel, timestamp: BigInt): Boolean = {
            DecisionMaker.CURRENCY_UPDATE_DURATION_MS < timestamp - state.lastCurrencyUpdateTime
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt): Option[TradeAction.TradeActionVal] = {
            None
        }
    }
}

object DecisionMaker {
    val CURRENCY_UPDATE_DURATION_MS: BigInt = 1000
    val MIN_PRICE_DIFF: BigInt = 20000
}
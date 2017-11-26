package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model._

class DecisionMaker(coinUnitExp: Int = 0) {
    case class Decisions(tradeAction: Option[TradeAction.TradeActionVal])

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt): Decisions = {
            Decisions(
                shouldTrade(state, timestamp, krwUnit)
            )
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt): Option[TradeAction.TradeActionVal] = {
            None
        }
    }
}

object DecisionMaker {
    val MIN_PRICE_DIFF: BigInt = 20000
}
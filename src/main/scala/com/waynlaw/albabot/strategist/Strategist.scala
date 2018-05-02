package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model._

class Strategist(krwUnit: BigInt = 1, coinUnitExp: Int = 0) {

    def compute(lastState: StrategistModel, event: Event.EventVal, timestamp: BigInt): (StrategistModel, List[Action.ActionVal]) = {
        val decisions = DecisionMaker.Decisions(lastState, event, timestamp, krwUnit, coinUnitExp)

        val nextState = StrategistModel(
            lastState.state.update(lastState, event, timestamp),
            KrwUpdater.update(lastState, event, decisions),
            CryptoCurrencyUpdater.update(lastState, event, timestamp, decisions),
            lastState.currencyRequester.update(event, timestamp),
            HistoryUpdater.update(lastState.history, event, timestamp),
            decisions
        )
        (nextState, ActionUpdater.evaluate(nextState, timestamp, decisions))
    }
}

object Strategist {

    val HISTORY_MINIMUM_FOR_TRADING: BigInt = 100
    val ORDER_RETRY_DURATION_MS: BigInt = 500
}

package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.{Action, Event, StrategistModel}

class Strategist {

    def compute(state: StrategistModel, event: Event.EventVal, timestamp: BigInt): (StrategistModel, Option[Action.ActionVal]) = {
        (state, Some(Action.RequestCurrency))
    }
}

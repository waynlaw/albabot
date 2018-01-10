package com.waynlaw.albabot.strategist.decision

import com.waynlaw.albabot.strategist.model.StrategistModel
import com.waynlaw.albabot.util.{MathUtil, RealNumber}

object DecisionUtil {
    def historyAngle(state: StrategistModel): Double = {
        MathUtil.computeAngle(MathUtil.removeNoise(state.history).map(x => x.copy(timestamp = x.timestamp / 1000)))
    }

    def buyableAmount(state: StrategistModel, wantedKrw: BigInt, coinUnitExp: Int): RealNumber = {
        val buyKrw = wantedKrw min state.krw
        RealNumber(buyKrw).divide(lastPrice(state), coinUnitExp)
    }

    def lastPrice(state: StrategistModel): BigInt = {
        state.history.lastOption.map(_.price).getOrElse(BigInt(1))
    }
}

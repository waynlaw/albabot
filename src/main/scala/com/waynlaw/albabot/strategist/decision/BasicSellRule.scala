package com.waynlaw.albabot.strategist.decision

import com.waynlaw.albabot.strategist.model.{StrategistModel, TradeAction}

case class BasicSellRule() extends TradeRule {
    override def update(state: StrategistModel, timestamp: BigInt): TradeRule = {
        BasicSellRule()
    }

    override def evaluate(state: StrategistModel, timestamp: BigInt): Option[TradeAction.TradeActionVal] = {
        None
    }
}

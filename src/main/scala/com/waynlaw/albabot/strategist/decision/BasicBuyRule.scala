package com.waynlaw.albabot.strategist.decision
import com.waynlaw.albabot.strategist.model.TradeAction.Buy
import com.waynlaw.albabot.strategist.model.{StrategistModel, TradeAction}
import com.waynlaw.albabot.util.RealNumber

case class BasicBuyRule() extends TradeRule {
    val BUY_IN_SINGLE_STEP: BigInt = 10 * 10000

    override def update(state: StrategistModel, timestamp: BigInt): TradeRule = {
        BasicBuyRule()
    }

    override def evaluate(state: StrategistModel, timestamp: BigInt): Option[TradeAction.TradeActionVal] = {
        val historyAngle = DecisionUtil.historyAngle(state)
        val angleFactor = BigInt((historyAngle / 50.0).toInt max 0)
        val wantedKrwAmount = BUY_IN_SINGLE_STEP * angleFactor
        val buyableAmount = DecisionUtil.buyableAmount(state, wantedKrwAmount)
        val lastPrice = DecisionUtil.lastPrice(state)

        if (50 <= historyAngle && RealNumber(0) < buyableAmount) {
            Some(Buy(buyableAmount, lastPrice))
        } else {
            None
        }
    }
}

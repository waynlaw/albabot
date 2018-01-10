package com.waynlaw.albabot.strategist.decision
import com.waynlaw.albabot.strategist.model.TradeAction.Buy
import com.waynlaw.albabot.strategist.model.{StrategistModel, TradeAction}
import com.waynlaw.albabot.util.RealNumber

case class BasicBuyRule(lastBuyTimestamp: BigInt = 0) extends TradeRule {
    val BUY_IN_SINGLE_STEP: BigInt = 10 * 10000
    val MIN_TRADE_TIME_STAMP: BigInt = 60 * 1000

    override def evaluate(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): (TradeRule, Option[TradeAction.TradeActionVal]) = {
        val historyAngle = DecisionUtil.historyAngle(state)
        val angleFactor = BigInt((historyAngle / 50.0).toInt max 0)
        val wantedKrwAmount = BUY_IN_SINGLE_STEP * angleFactor
        val buyableAmount = DecisionUtil.buyableAmount(state, wantedKrwAmount, coinUnitExp)
        val lastPrice = DecisionUtil.lastPrice(state)
        val isEnoughTimeDiff = timestamp - lastBuyTimestamp >= MIN_TRADE_TIME_STAMP

        if (50 <= historyAngle && RealNumber(0) < buyableAmount && isEnoughTimeDiff) {
            (copy(lastBuyTimestamp = timestamp), Some(Buy(buyableAmount, lastPrice)))
        } else {
            (this, None)
        }
    }
}

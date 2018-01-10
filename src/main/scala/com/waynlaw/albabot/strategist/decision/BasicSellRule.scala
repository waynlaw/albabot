package com.waynlaw.albabot.strategist.decision

import com.waynlaw.albabot.strategist.model.TradeAction.Sell
import com.waynlaw.albabot.strategist.model.{CryptoCurrencyState, StrategistModel, TradeAction}
import com.waynlaw.albabot.util.RealNumber

case class BasicSellRule() extends TradeRule {
    val MIN_PRICE_DIFF: BigInt = 100000

    override def update(state: StrategistModel, timestamp: BigInt): TradeRule = {
        BasicSellRule()
    }

    override def evaluate(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): Option[TradeAction.TradeActionVal] = {
        val historyAngle = DecisionUtil.historyAngle(state)
        val lastPrice = DecisionUtil.lastPrice(state)
        val sellCount = profitedSellCount(state, lastPrice, coinUnitExp)
        if (0 >= historyAngle && RealNumber(0) < sellCount) {
            Some(Sell(sellCount, lastPrice))
        } else {
            None
        }
    }

    private def profitedSellCount(state: StrategistModel, lastPrice: BigInt, coinUnitExp: Int): RealNumber = {
        val sellItems = state.cryptoCurrency.filter(x => lastPrice - x.price >= MIN_PRICE_DIFF && x.state == CryptoCurrencyState.Nothing)
        sellItems.map(_.amount).sum.floor(coinUnitExp)
    }
}

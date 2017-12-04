package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.model.coin.CoinType
import com.waynlaw.albabot.strategist.model.TradeAction.{Buy, Sell}
import com.waynlaw.albabot.strategist.model._
import com.waynlaw.albabot.util.{MathUtil, RealNumber}

class DecisionMaker(coinType: CoinType.Coin) {
    case class Decisions(tradeAction: Option[TradeAction.TradeActionVal])

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt): Decisions = {
            Decisions(
                shouldTrade(state, timestamp, krwUnit)
            )
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt): Option[TradeAction.TradeActionVal] = {
            val normalInfos = MathUtil.removeNoise(state.history)
            val avg = if (0 == normalInfos.length) {
                BigInt(0)
            } else {
                normalInfos.map(x => x.price).sum / normalInfos.length
            }
            val lastPrice = state.history.lastOption.map(_.price / krwUnit * krwUnit).getOrElse(BigInt(1))
            val buyableAmount = RealNumber(state.krw).divide(lastPrice, coinType.minUnitExp)
            state.state match {
                case _: State.Init | _: State.WaitingCurrencyInfo =>
                    None
                case _ if state.history.length < Strategist.HISTORY_MINIMUM_FOR_TRADING =>
                    None
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToBuy]) =>
                    None
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToSell]) =>
                    None
                case _ if lastPrice < avg * 9 / 10 && RealNumber(0) != buyableAmount =>
                    Some(Buy(buyableAmount, lastPrice))
                case _ if lastPrice > avg * 11 / 10 && RealNumber(0) != sellItemCount(state, lastPrice, coinType.minUnitExp) =>
                    val sellCount = sellItemCount(state, lastPrice, coinType.minUnitExp)
                    Some(Sell(sellCount, lastPrice))
                case _ =>
                    None
            }
        }
    }

    private def sellItemCount(state: StrategistModel, lastPrice: BigInt, coinUnitExp: Int): RealNumber = {
        state.cryptoCurrency.map(_.amount).sum.round(coinUnitExp)
    }
}

object DecisionMaker {
    val MIN_PRICE_DIFF: BigInt = 20000
}
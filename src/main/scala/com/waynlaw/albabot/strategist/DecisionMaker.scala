package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.decision.{BasicBuyRule, BasicSellRule, TradeRule}
import com.waynlaw.albabot.strategist.model._

object DecisionMaker {
    case class Decisions(tradeAction: Option[TradeAction.TradeActionVal] = None, tradeRules: List[TradeRule] = Decisions.rules())

    object Decisions {
        def apply(state: StrategistModel, event: Event.EventVal, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): Decisions = {
            shouldTrade(state, timestamp, krwUnit, coinUnitExp)
        }

        /**
          * 사용할 기본 전략. 왼쪽에 있을수록 먼저 평가된다.
          */
        def rules(): List[TradeRule] = {
            List(BasicBuyRule(), BasicSellRule())
        }

        /**
          * 현재 거래가능 상태인지 유무를 판단한다.
          */
        def isTradeable(state: StrategistModel): Boolean = {
            state.state match {
                case _: State.Init | _: State.WaitingCurrencyInfo =>
                    false
                case _ if state.history.length < Strategist.HISTORY_MINIMUM_FOR_TRADING =>
                    false
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToBuy]) =>
                    false
                case _ if state.cryptoCurrency.exists(x => x.state.isInstanceOf[CryptoCurrencyState.TryToSell]) =>
                    false
                case _ =>
                    true
            }
        }

        def shouldTrade(state: StrategistModel, timestamp: BigInt, krwUnit: BigInt, coinUnitExp: Int): Decisions = {
            if (isTradeable(state)) {
                val rulesAndDecisions = state.decisions.tradeRules.map(_.evaluate(state, timestamp, krwUnit, coinUnitExp))
                val newRules = rulesAndDecisions.map(_._1)
                val decision = rulesAndDecisions.map(_._2).find(x => x.nonEmpty).flatten
                state.decisions.copy(tradeAction = decision, tradeRules = newRules)
            } else {
                state.decisions.copy(tradeAction = None, tradeRules = state.decisions.tradeRules)
            }
        }
    }
}
package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.strategist.{BalanceRequester, Strategist}

object State {
    sealed trait StateVal {
        def update(strategistModel: StrategistModel, event: Event.EventVal, timestamp: BigInt): StateVal = {
            this
        }
    }

    case class Init(balanceRequester: BalanceRequester = BalanceRequester()) extends StateVal {
        override def update(strategistModel: StrategistModel, event: Event.EventVal, timestamp: BigInt): StateVal = {
            event match {
                case _: Event.ReceiveUserBalance =>
                    WaitingCurrencyInfo()
                case _ =>
                    Init(
                        balanceRequester.update(event, timestamp)
                    )
            }
        }
    }

    case class WaitingCurrencyInfo() extends StateVal {
        override def update(strategistModel: StrategistModel, event: Event.EventVal, timestamp: BigInt): StateVal = {
            if (Strategist.HISTORY_MINIMUM_FOR_TRADING <= strategistModel.history.length) {
                Trading
            } else {
                this
            }
        }
    }

    case object Trading extends StateVal
}
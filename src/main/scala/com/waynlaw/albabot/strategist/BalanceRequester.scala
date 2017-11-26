package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.{Event, State, StrategistModel}

/*
 * 사용자의 잔고는 기동 직후 1회 요청한다.
 * 요청 후 일정 시간이 지나면 다시 요청한다.
 */
case class BalanceRequester(
    willRequestUserBalance: Boolean = false,
    lastBalanceRequestTime: BigInt = 0,
    lastBalanceUpdateTime: BigInt = 0,
) {
    def update(event: Event.EventVal, timestamp: BigInt): BalanceRequester = {
        val willRequestUserBalance = shouldRequestUserBalance(timestamp)
        BalanceRequester(
            willRequestUserBalance,
            lastBalanceRequestTime(willRequestUserBalance, timestamp),
            lastBalanceUpdateTime(event, timestamp)
        )
    }

    private def shouldRequestUserBalance(timestamp: BigInt): Boolean = {
        0 == lastBalanceUpdateTime &&
            BalanceRequester.BALANCE_UPDATE_REQUEST_DURATION_MS < timestamp - lastBalanceRequestTime
    }

    private def lastBalanceRequestTime(willRequestUserBalance: Boolean, timestamp: BigInt): BigInt = {
        if (willRequestUserBalance) {
            timestamp
        } else {
            lastBalanceRequestTime
        }
    }

    private def lastBalanceUpdateTime(event: Event.EventVal, timestamp: BigInt): BigInt = {
        event match {
            case _: Event.ReceiveUserBalance =>
                timestamp
            case _ =>
                lastBalanceUpdateTime
        }
    }
}

object BalanceRequester {
    val BALANCE_UPDATE_REQUEST_DURATION_MS: BigInt = 500
}
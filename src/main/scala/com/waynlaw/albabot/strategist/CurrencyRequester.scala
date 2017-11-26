package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.Event

/*
 * 화폐 정보는 매초 요청한다.
 */
case class CurrencyRequester(
    willRequestUserCurrency: Boolean = false,
    lastCurrencyRequestTime: BigInt = 0,
    lastCurrencyUpdateTime: BigInt = 0,
) {
    def update(event: Event.EventVal, timestamp: BigInt): CurrencyRequester = {
        val willRequestCurrency = shouldRequestCurrency(timestamp)
        CurrencyRequester(
            willRequestCurrency,
            lastCurrencyRequestTime(willRequestCurrency, timestamp),
            lastCurrencyUpdateTime(event, timestamp)
        )
    }

    private def shouldRequestCurrency(timestamp: BigInt): Boolean = {
        CurrencyRequester.CURRENCY_UPDATE_DURATION_MS < timestamp - lastCurrencyUpdateTime
    }

    private def lastCurrencyRequestTime(willRequestCurrency: Boolean, timestamp: BigInt): BigInt = {
        if (willRequestCurrency) {
            timestamp
        } else {
            lastCurrencyRequestTime
        }
    }

    private def lastCurrencyUpdateTime(event: Event.EventVal, timestamp: BigInt): BigInt = {
        event match {
            case _: Event.ReceivePrice =>
                timestamp
            case _ =>
                lastCurrencyUpdateTime
        }
    }
}

object CurrencyRequester {
    val CURRENCY_UPDATE_DURATION_MS: BigInt = 1000
}

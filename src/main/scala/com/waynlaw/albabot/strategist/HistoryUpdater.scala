package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.{CurrencyInfo, Event}

object HistoryUpdater {
    val HISTORY_KEEP_DURATION_MS: BigInt = 5 * 60 * 1000

    def update(history: Array[CurrencyInfo], event: Event.EventVal, timestamp: BigInt): Array[CurrencyInfo] = {
        event match {
            case Event.ReceivePrice(time, cryptoCurrency) =>
                (history :+ CurrencyInfo(cryptoCurrency, time))
                    .filter(isHistoryInDuration(timestamp))
                    .sortBy(_.timestamp)
            case _ =>
                history
        }
    }

    private def isHistoryInDuration(timestamp: BigInt)(currencyInfo: CurrencyInfo): Boolean = {
        timestamp - currencyInfo.timestamp <= HistoryUpdater.HISTORY_KEEP_DURATION_MS
    }
}

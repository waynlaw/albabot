package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.{CurrencyInfo, Event}

/*
 * 화폐 정보는 일정 기간만 보관하고 항상 시간 순서대로 저장한다.
 */
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

package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.strategist.BalanceRequester

case class StrategistModel(
    state: State.StateVal = State.Init,
    krw: BigInt = 0,
    cryptoCurrency: List[CryptoCurrencyInfo] = Nil,
    lastCurrencyRequestTime: BigInt = 0,
    lastCurrencyUpdateTime: BigInt = 0,
    balanceRequester: BalanceRequester = BalanceRequester(),
    history: Array[CurrencyInfo] = Array()
) {

}

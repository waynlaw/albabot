package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.strategist.{BalanceRequester, CurrencyRequester}

case class StrategistModel(
    state: State.StateVal = State.Init(),
    krw: BigInt = 0,
    cryptoCurrency: List[CryptoCurrencyInfo] = Nil,
    currencyRequester: CurrencyRequester = CurrencyRequester(),
    history: Array[CurrencyInfo] = Array()
) {

}


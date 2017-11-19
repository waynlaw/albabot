package com.waynlaw.albabot.strategist.model

case class StrategistModel(
    state: State.StateVal = State.Init,
    krw: BigInt = 0,
    cryptoCurrency: List[CryptoCurrencyInfo] = Nil,
    lastCurrencyRequestTime: BigInt = 0,
    lastCurrencyUpdateTime: BigInt = 0,
    lastBalanceRequestTime: BigInt = 0,
    lastBalanceUpdateTime: BigInt = 0,
    history: Array[CurrencyInfo] = Array()
) {

}

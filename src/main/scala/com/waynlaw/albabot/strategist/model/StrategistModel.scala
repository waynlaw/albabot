package com.waynlaw.albabot.strategist.model

case class StrategistModel(
    state: State.StateVal = State.Init,
    krw: BigInt = 0,
    cryptoCurrency: BigInt = 0,
    history: Array[CurrencyInfo] = Array()
) {

}

package com.waynlaw.albabot.strategist.model

import com.waynlaw.albabot.strategist.{CurrencyRequester, DecisionMaker}

case class StrategistModel(
    state: State.StateVal = State.Init(),
    krw: BigInt = 0,
    cryptoCurrency: List[CryptoCurrencyInfo] = Nil,
    currencyRequester: CurrencyRequester = CurrencyRequester(),
    history: Array[CurrencyInfo] = Array(),
    decisions: DecisionMaker.Decisions = DecisionMaker.Decisions(None, isBuyable = false, 0)
) {

}


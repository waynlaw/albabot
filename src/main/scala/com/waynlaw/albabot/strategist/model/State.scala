package com.waynlaw.albabot.strategist.model

object State {
    sealed trait StateVal
    case object Init extends StateVal
    case object WaitingCurrencyInfo extends StateVal
    case object Trading extends StateVal
}
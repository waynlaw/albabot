package com.waynlaw.albabot.strategist.model

object Action {
    sealed trait ActionVal
    case object RequestCurrency extends ActionVal
}


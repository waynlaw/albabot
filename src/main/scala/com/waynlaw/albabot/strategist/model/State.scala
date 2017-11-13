package com.waynlaw.albabot.strategist.model

object State {
    sealed trait StateVal
    case object Init extends StateVal
    case object Waiting extends StateVal
}
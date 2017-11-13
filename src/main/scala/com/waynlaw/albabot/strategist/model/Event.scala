package com.waynlaw.albabot.strategist.model

object Event {
    sealed trait EventVal
    case object Tick extends EventVal
    case class ReceivePrice() extends EventVal
}


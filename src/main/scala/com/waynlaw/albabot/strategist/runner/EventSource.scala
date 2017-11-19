package com.waynlaw.albabot.strategist.runner

import com.waynlaw.albabot.strategist.model.Event

trait EventSource {
    def fetchEvent: Option[Event.EventVal]
}

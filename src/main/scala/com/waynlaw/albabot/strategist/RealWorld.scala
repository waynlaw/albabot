package com.waynlaw.albabot.strategist

import com.waynlaw.albabot.strategist.model.Action.RequestCurrency
import com.waynlaw.albabot.strategist.model.{Action, Event}
import com.waynlaw.albabot.strategist.runner.{Actor, EventSource}
import com.waynlaw.albabot.util.BithumbApi

class RealWorld() extends EventSource with Actor {

    var eventQueue: List[Event.EventVal] = List()

    override def fetchEvent: Option[Event.EventVal] = {
        None
    }

    override def run(action: Action.ActionVal): Unit = {
        action match {
            case RequestCurrency =>
                val info = BithumbApi.getLastInfo("BTC") // TODO: will fix async
        }
    }
}

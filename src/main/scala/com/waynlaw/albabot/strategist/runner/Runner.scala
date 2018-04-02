package com.waynlaw.albabot.strategist.runner

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.strategist.model.{Action, Event, StrategistModel}
import com.waynlaw.albabot.util.PrettyPrint
import com.waynlaw.albabot.view.Display

object Runner {
    val MAX_TICK_MS = 2000
    val MIN_TICK_MS = 300
}

class Runner( state: StrategistModel,
                evaluator: (StrategistModel, Event.EventVal, BigInt) => (StrategistModel, List[Action.ActionVal]),
                eventSource: EventSource,
                actor: Actor
            ) extends Thread with LazyLogging {

    val display = new Display()

    override def run(): Unit = {
        var lastState = state

        while (display.isRunning()) {
            val timestamp = System.currentTimeMillis()

            val event = eventSource.fetchEvent.getOrElse(Event.Tick)
            val (newState, actionList) = evaluator(lastState, event, timestamp)
            lastState = newState

            logger.debug(s"\nTime: $timestamp] $actionList, $event\n${PrettyPrint.prettyPrint(lastState)} ->\n${PrettyPrint.prettyPrint(newState)} ")
            logger.debug(s"history num : ${newState.history.length}")

            display.show(newState)

            for {
                action <- actionList
            } actor.run(action)

            Thread.sleep(1000L)
        }
    }
}

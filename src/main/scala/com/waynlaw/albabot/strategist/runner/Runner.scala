package com.waynlaw.albabot.strategist.runner

import com.waynlaw.albabot.strategist.model.{Event, Action, StrategistModel}

object Runner {
    val MAX_TICK_MS = 1000
    val MIN_TICK_MS = 200
}

class Runner(
                state: StrategistModel,
                evaluator: (StrategistModel, Event.EventVal, BigInt) => (StrategistModel, Option[Action.ActionVal]),
                eventSource: EventSource,
                actor: Actor
            ) extends Thread {

    def nextTick(event: Event.EventVal, actionOption: Option[Action.ActionVal], tick: Int): Int = {
        val newTick = (event, actionOption) match {
            case (Event.Tick, None) =>
                tick * 2
            case _ =>
                tick / 2
        }
        Runner.MIN_TICK_MS max (Runner.MAX_TICK_MS min newTick)
    }

    override def run(): Unit = {
        var tick = Runner.MIN_TICK_MS
        var lastState = state

        while (true) {
            val event = eventSource.fetchEvent.getOrElse(Event.Tick)
            val timestamp = System.currentTimeMillis()
            val (newState, actionOption) = evaluator(lastState, event, timestamp)

            println(s"$timestamp] $actionOption : $state -> $newState ")

            for {
                action <- actionOption
            } actor.run(action)

            tick = nextTick(event, actionOption, tick)
            lastState = newState

            Thread.sleep(tick)
        }
    }
}

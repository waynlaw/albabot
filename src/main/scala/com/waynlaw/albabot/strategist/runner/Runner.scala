package com.waynlaw.albabot.strategist.runner

import com.waynlaw.albabot.strategist.model.{Action, Event, StrategistModel}
import com.waynlaw.albabot.strategist.model.{Action, Event, StrategistModel}
import com.waynlaw.albabot.util.{MathUtil, PrettyPrint}

object Runner {
    val MAX_TICK_MS = 2000
    val MIN_TICK_MS = 1000
}

class Runner(
                state: StrategistModel,
                evaluator: (StrategistModel, Event.EventVal, BigInt) => (StrategistModel, List[Action.ActionVal]),
                eventSource: EventSource,
                actor: Actor
            ) extends Thread {

    def nextTick(event: Event.EventVal, actionList: List[Action.ActionVal], tick: Int): Int = {
        val newTick = (event, actionList) match {
            case (Event.Tick, Nil) =>
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
            val (newState, actionList) = evaluator(lastState, event, timestamp)
            val angle = MathUtil.computeAngle(MathUtil.removeNoise(lastState.history).map(x => x.copy(timestamp = x.timestamp / 100000)))

            println(s"\nTime: $timestamp] angle:$angle $actionList, $event\n${PrettyPrint.prettyPrint(lastState)} ->\n${PrettyPrint.prettyPrint(newState)} ")
            println(s"history num : ${newState.history.length}")

            for {
                action <- actionList
            } actor.run(action)

            tick = nextTick(event, actionList, tick)
            lastState = newState

            Thread.sleep(tick)
        }
    }
}

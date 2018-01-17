package com.waynlaw.albabot.strategist.runner

import com.typesafe.scalalogging.LazyLogging
import com.waynlaw.albabot.strategist.model.{Action, Event, StrategistModel}
import com.waynlaw.albabot.util.{MathUtil, PrettyPrint}

object Runner {
    val MAX_TICK_MS = 2000
    val MIN_TICK_MS = 300
}

class Runner( state: StrategistModel,
                evaluator: (StrategistModel, Event.EventVal, BigInt) => (StrategistModel, List[Action.ActionVal]),
                eventSource: EventSource,
                actor: Actor
            ) extends Thread with LazyLogging {
//
//    def nextTick(event: Event.EventVal, actionList: List[Action.ActionVal], tick: Int): Int = {
//        val newTick = (event, actionList) match {
//            case (Event.Tick, Nil) =>
//                tick * 2
//            case _ =>
//                tick / 2
//        }
//        Runner.MIN_TICK_MS max (Runner.MAX_TICK_MS min newTick)
//    }

    override def run(): Unit = {
//        var tick = Runner.MIN_TICK_MS
        var lastState = state

        while (true) {
            val timestamp = System.currentTimeMillis()

            val event = eventSource.fetchEvent.getOrElse(Event.Tick)
            val (newState, actionList) = evaluator(lastState, event, timestamp)
            lastState = newState

            logger.debug(s"\nTime: $timestamp] $actionList, $event\n${PrettyPrint.prettyPrint(lastState)} ->\n${PrettyPrint.prettyPrint(newState)} ")
            logger.debug(s"history num : ${newState.history.length}")

            for {
                action <- actionList
            } actor.run(action)

//            tick = nextTick(event, actionList, tick)

            Thread.sleep(1000L)
        }
    }
}

package com.waynlaw.albabot.view

import java.text.SimpleDateFormat
import java.util.Date

import com.waynejo.terminal.layout.LayoutBuilder
import com.waynejo.terminal.terminal.TerminalCommand
import com.waynejo.terminal.terminal.manager.{RuntimeManager, StdInManager, TerminalManager}
import com.waynlaw.albabot.strategist.model.StrategistModel

class Display {
  var callback: () => Vector[TerminalCommand] = () => Vector[TerminalCommand]()

  val builder = LayoutBuilder()
  val runtimeManager = new RuntimeManager()
  val stdInManager = new StdInManager()
  val terminal = new TerminalManager(builder, _ => {
    callback()
    Vector()
  }, runtimeManager.channel, stdInManager.channel)

  new Thread(() => {
    terminal.run()
  }).start()

  def show(newState: StrategistModel){

    callback = () => {
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

      println(s"Date : ${dateFormat.format(new Date())}\r")
      println(s"KRW : ${newState.krw}\r")
      println(s"Currency : ${newState.cryptoCurrency.mkString(", ")}\r")

      println("history : \r")
      val showingLength = 10 min newState.history.length
      (0 until showingLength).foreach(idx => {
        val priceInfo = newState.history(idx)
        println(s"${priceInfo.timestamp}] ${priceInfo.price}\r")
      })
      Vector[TerminalCommand]()
    }
  }

  def isRunning: Boolean = {
    terminal.isRunning
  }
}

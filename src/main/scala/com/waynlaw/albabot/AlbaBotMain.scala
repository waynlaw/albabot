package com.waynlaw.albabot

import com.waynlaw.albabot.util.BithumbApi

object AlbaBotMain{
  def main(args: Array[String]) {
    Console println Configure.load()

    Console println BithumbApi.getLastInfo("BTC")
  }
}
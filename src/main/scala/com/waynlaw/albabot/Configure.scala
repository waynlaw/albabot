package com.waynlaw.albabot

import org.json4s.DefaultFormats
import org.json4s._
import org.json4s.native.JsonMethods._

case class Configure(apiKey: String, secretKey: String, baseUrl: String, collector: CollectorConfigure)

case class CollectorConfigure(tick: Long = 1000)

object Configure {
    def load(): Configure = {
        val resourcesPath = getClass.getResource("/configure.json")
        val jsonString = io.Source.fromFile(resourcesPath.getPath).mkString

        implicit val formats = DefaultFormats

        parse(jsonString).extract[Configure]
    }
}
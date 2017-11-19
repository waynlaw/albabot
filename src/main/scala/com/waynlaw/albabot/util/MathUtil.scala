package com.waynlaw.albabot.util

import com.waynlaw.albabot.strategist.model.CurrencyInfo

object MathUtil {
    def removeNoise(data: Array[CurrencyInfo]): Array[CurrencyInfo] = {
        val minIdx = (data.length + 10 - 1) / 10
        val maxIdx = data.length * 9 / 10

        data.sortBy(_.price)
            .slice(minIdx, maxIdx)
            .sortBy(_.timestamp)
    }

    def computeAngle(data: Array[CurrencyInfo]): Double = {
        val a = data.map(x => x.timestamp * x.timestamp).sum
        val b = data.map(_.timestamp).sum
        val c = b
        val d = BigInt(data.length)

        val q1 = data.map(x => x.timestamp * x.price).sum
        val q2 = data.map(x => x.price).sum

        if (0 == a * d - b * c) {
            0.0
        } else {
            (d * q1 - b * q2).toDouble / (a * d - b * c).toDouble
        }
    }
}

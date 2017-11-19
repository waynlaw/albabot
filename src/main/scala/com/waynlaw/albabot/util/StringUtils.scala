package com.waynlaw.albabot.util

object StringUtils {

    val SATOSHI_UNIT = 100000000
    val SATOSHI_LENGTH = 8

    def BTCStringToBigInt(s: String): BigInt = {
        if (s.contains(".")) {
            val Array(bitcoin, satoshi) = s.split("\\.")
            BigInt(bitcoin) * SATOSHI_UNIT + BigInt(satoshi + ("0" * (SATOSHI_LENGTH - satoshi.length)))
        } else {
            BigInt(s) * SATOSHI_UNIT
        }
    }

    def BigIntToBTCString(bitcoin: BigInt): String = {
        val intPart = bitcoin / BigInt(SATOSHI_UNIT)
        val belowPart = (bitcoin - (intPart * BigInt(SATOSHI_UNIT))).toString()
        val resultWithZero = s"$intPart.${("0" * (SATOSHI_LENGTH - belowPart.length)) + belowPart}"
        val zeropart = resultWithZero.reverse.takeWhile(_ == '0')
        val result = resultWithZero.substring(0, resultWithZero.length - zeropart.length)
        if (result.last == '.') {
            result.init
        } else {
            result
        }
    }
}

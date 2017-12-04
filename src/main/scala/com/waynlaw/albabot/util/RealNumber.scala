package com.waynlaw.albabot.util

import scala.annotation.tailrec
import scala.math.Numeric

trait RealNumberIsNumeric extends Numeric[RealNumber] {
    def plus(x: RealNumber, y: RealNumber): RealNumber = {
        x + y
    }

    def minus(x: RealNumber, y: RealNumber): RealNumber = {
        x - y
    }

    def times(x: RealNumber, y: RealNumber): RealNumber = {
        x * y
    }

    def negate(x: RealNumber): RealNumber = {
        -x
    }

    def fromInt(x: Int): RealNumber = {
        ???
    }

    def toInt(x: RealNumber): Int = {
        ???
    }

    def toLong(x: RealNumber): Long = {
        ???
    }

    def toFloat(x: RealNumber): Float = {
        ???
    }

    def toDouble(x: RealNumber): Double = {
        ???
    }

    override def zero: RealNumber = {
        RealNumber(0)
    }

    override def one: RealNumber = {
        RealNumber(1)
    }
}

trait RealNumberOrdering extends scala.math.Ordering[RealNumber] {
    override def compare(a: RealNumber, b: RealNumber): Int = {
        ???
    }
}

case class RealNumber(number: BigInt, exponent: BigInt = 0) {

    def +(rhs: RealNumber): RealNumber = {
        numericOperation(rhs, _ + _)
    }

    def -(rhs: RealNumber): RealNumber = {
        numericOperation(rhs, _ - _)
    }

    def * (rhs: BigInt): RealNumber = {
        copy(number = number * rhs)
    }

    def unary_-(): RealNumber = {
        copy(number = -number)
    }

    def divide(number: BigInt, limitExponent: BigInt): RealNumber = {
        if (exponent <= limitExponent) {
            copy(number = this.number / RealNumber.pow10(limitExponent - exponent) / number, limitExponent)
        } else {
            copy(number = this.number * RealNumber.pow10(exponent - limitExponent) / number, limitExponent)
        }
    }

    def divide(number: RealNumber, limitExponent: BigInt): RealNumber = {
        val newExponent = exponent - number.exponent
        if (newExponent <= limitExponent) {
            copy(number = this.number / RealNumber.pow10(limitExponent - newExponent) / number.number, limitExponent)
        } else {
            copy(number = this.number * RealNumber.pow10(newExponent - limitExponent) / number.number, limitExponent)
        }
    }

    def < (rhs: RealNumber): Boolean = {
        booleanOperation(rhs, _ < _)
    }

    def <= (rhs: RealNumber): Boolean = {
        booleanOperation(rhs, _ <= _)
    }

    def == (rhs: RealNumber): Boolean = {
        booleanOperation(rhs, _ == _)
    }

    def != (rhs: RealNumber): Boolean = {
        booleanOperation(rhs, _ != _)
    }

    override def equals(obj: scala.Any): Boolean = {
        obj match {
            case rhs: RealNumber =>
                booleanOperation(rhs, _ == _)
            case _ =>
                false
        }
    }

    private def numericOperation[A](rhs: RealNumber, func:(BigInt, BigInt) => BigInt): RealNumber = {
        if (exponent < rhs.exponent) {
            copy(number = func(number, rhs.number * RealNumber.pow10(rhs.exponent - exponent)), exponent = exponent)
        } else if (exponent > rhs.exponent) {
            copy(number = func(number * RealNumber.pow10(exponent - rhs.exponent), rhs.number), exponent = rhs.exponent)
        } else {
            copy(number = func(number, rhs.number), exponent = exponent)
        }
    }

    private def booleanOperation[A](rhs: RealNumber, func:(BigInt, BigInt) => Boolean): Boolean = {
        if (exponent < rhs.exponent) {
            func(number, rhs.number * RealNumber.pow10(rhs.exponent - exponent))
        } else if (exponent > rhs.exponent) {
            func(number * RealNumber.pow10(exponent - rhs.exponent), rhs.number)
        } else {
            func(number, rhs.number)
        }
    }

    def toBigInt: BigInt = {
        if (0 <= exponent) {
            number * RealNumber.pow10(exponent)
        } else {
            (number + RealNumber.pow10(-exponent) / 2) / RealNumber.pow10(-exponent)
        }
    }

    def round(exponent: BigInt): RealNumber = {
        if (this.exponent >= exponent) {
            this
        } else {
            val exponentDiff = exponent - this.exponent
            copy(number = (number + RealNumber.pow10(exponentDiff) / 2) / RealNumber.pow10(exponentDiff), exponent)
        }
    }

    def floor(exponent: BigInt): RealNumber = {
        if (this.exponent >= exponent) {
            this
        } else {
            val exponentDiff = exponent - this.exponent
            copy(number = number / RealNumber.pow10(exponentDiff), exponent)
        }
    }

    override def toString: String = {
        val intPart = number / RealNumber.pow10(-exponent.toInt)
        val belowPart = (number - (intPart * RealNumber.pow10(-exponent))).toString
        val resultWithZero = s"$intPart.${("0" * (-exponent - belowPart.length).toInt) + belowPart}"
        val zeroPart = resultWithZero.reverse.takeWhile(_ == '0')
        val result = resultWithZero.substring(0, resultWithZero.length - zeroPart.length)
        if (result.last == '.') {
            result.init
        } else {
            result
        }
    }
}

object RealNumber {

    implicit object RealNumberIsNumeric extends RealNumberIsNumeric with RealNumberOrdering

    def apply(s: String): RealNumber = {
        if (s.contains(".")) {
            val Array(integerPart, realPart) = s.split("\\.")
            RealNumber(BigInt(integerPart) * pow10(realPart.length) + BigInt(realPart), -realPart.length)
        } else {
            RealNumber(BigInt(s))
        }
    }

    @tailrec
    private def pow10(exponent: BigInt, acc: BigInt = 1): BigInt = {
        if (0 >= exponent) {
            acc
        } else {
            pow10(exponent - 1, acc * 10)
        }
    }
}
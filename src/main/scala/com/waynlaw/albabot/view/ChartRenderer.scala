package com.waynlaw.albabot.view

import com.waynlaw.albabot.strategist.model.CurrencyInfo

class ChartRenderer {

  var width: Int = 80 // size for test
  var height: Int = 20
  var displayBuffer: Array[Array[Char]] = Array.ofDim[Char](height, width)

  def display(currencyInfo: Array[CurrencyInfo]): Unit = {
    if (0 == width || 0 == height) {
      return
    }
    updateDisplayBuffer(currencyInfo)
    printDisplayBuffers()
  }

  private def updateDisplayBuffer(currencyInfo: Array[CurrencyInfo]) {
    val graphWidth = width
    val skipNum = currencyInfo.length - graphWidth
    val displayItems = currencyInfo.drop(skipNum).map(_.price)
    val graphBottom = if (displayItems.isEmpty) BigInt(0) else displayItems.min
    val displayItemMax = if (displayItems.isEmpty) BigInt(0) else displayItems.max
    val graphTop = (graphBottom + 1) max displayItemMax
    val graphBottomWithMargin = graphBottom - (graphTop - graphBottom) / 10
    val graphTopWithMargin = graphTop + (graphTop - graphBottom) / 10
    val graphHeightWithMargin = graphTopWithMargin - graphBottomWithMargin
    for {
      y <- 0 until height
      x <- 0 until width
    } {
      if (x < displayItems.length) {
        if (height - y - 1 <= height * (displayItems(x) - graphBottomWithMargin) / graphHeightWithMargin) {
          displayBuffer(y)(x) = '*'
        } else {
          displayBuffer(y)(x) = ' '
        }
      } else {
        displayBuffer(y)(x) = ' '
      }
    }
  }

  private def printDisplayBuffers(){
    for {
      y <- 0 until height
    } {
      println(displayBuffer(y).mkString("") + "\r")
    }
  }
}

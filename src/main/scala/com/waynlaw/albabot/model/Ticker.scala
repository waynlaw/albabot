package com.waynlaw.albabot.model

import com.waynlaw.albabot.api.database.model.DatabaseTickInfo

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 13.
  * @note:
  */

/*
{
    "status": "0000",
    "data": {
        "opening_price" : "504000",
        "closing_price" : "505000",
        "min_price"     : "504000",
        "max_price"     : "516000",
        "average_price" : "509533.3333",
        "units_traded"  : "14.71960286",
        "volume_1day"   : "14.71960286",
        "volume_7day"   : "15.81960286",
        "buy_price"     : "505000",
        "sell_price"    : "504000",
        "date"          : 1417141032622
    }
}
*/

case class TickerData(
                       openingPrice: String,
                       closingPrice: String,
                       minPrice: String,
                       maxPrice: String,
                       averagePrice: String,
                       unitsTraded: String,
                       volume1day: String,
                       volume7day: String,
                       buyPrice: String,
                       sellPrice: String,
                       date: String
                     )

object TickerData {
  def from(tickerWithoutDate: TickerWithoutDate, date: String): TickerData = {
    TickerData(
      tickerWithoutDate.openingPrice,
      tickerWithoutDate.closingPrice,
      tickerWithoutDate.minPrice,
      tickerWithoutDate.maxPrice,
      tickerWithoutDate.averagePrice,
      tickerWithoutDate.unitsTraded,
      tickerWithoutDate.volume1day,
      tickerWithoutDate.volume7day,
      tickerWithoutDate.buyPrice,
      tickerWithoutDate.sellPrice,
      date
    )
  }

  def from(databaseTickInfo: DatabaseTickInfo): TickerData = {
    TickerData(
      databaseTickInfo.openingPrice.toString,
      databaseTickInfo.closingPrice.toString,
      databaseTickInfo.minPrice.toString,
      databaseTickInfo.maxPrice.toString,
      databaseTickInfo.averagePrice.toString,
      databaseTickInfo.unitsTraded.toString,
      databaseTickInfo.volume1day.toString,
      databaseTickInfo.volume7day.toString,
      databaseTickInfo.buyPrice.toString,
      databaseTickInfo.sellPrice.toString,
      javax.xml.bind.DatatypeConverter.parseDateTime(databaseTickInfo.regDate).getTimeInMillis.toString
    )
  }
}

case class TickerWithoutDate(
                              openingPrice: String,
                              closingPrice: String,
                              minPrice: String,
                              maxPrice: String,
                              averagePrice: String,
                              unitsTraded: String,
                              volume1day: String,
                              volume7day: String,
                              buyPrice: String,
                              sellPrice: String
                            )

case class Ticker(status: String, data: TickerData)

case class TickerAll(status: String, data: Map[String, TickerData])

/*
case class Ticker(
                         openingPrice: BigInt,
                         closingPrice: BigInt,
                         minPrice: BigInt,
                         maxPrice: BigInt,
                         averagePrice: BigInt,
                         unitsTraded: BigInt,
                         volume1day: BigInt,
                         volume7day: BigInt,
                         buyPrice: BigInt,
                         sellPrice: BigInt,
                         date: String
                       ) {

  def this(openingPrice: String,
           closingPrice: String,
           minPrice: String,
           maxPrice: String,
           averagePrice: String,
           unitsTraded: String,
           volume1day: String,
           volume7day: String,
           buyPrice: String,
           sellPrice: String,
           date: String) =
    this(
      BigInt((openingPrice.toDouble * 100000000).toInt),
      BigInt((closingPrice.toDouble * 100000000).toInt),
      BigInt((minPrice.toDouble * 100000000).toInt),
      BigInt((maxPrice.toDouble * 100000000).toInt),
      BigInt((averagePrice.toDouble * 100000000).toInt),
      BigInt((unitsTraded.toDouble * 100000000).toInt),
      BigInt((volume1day.toDouble * 100000000).toInt),
      BigInt((volume7day.toDouble * 100000000).toInt),
      BigInt((buyPrice.toDouble * 100000000).toInt),
      BigInt((sellPrice.toDouble * 100000000).toInt),
      date)
}

*/
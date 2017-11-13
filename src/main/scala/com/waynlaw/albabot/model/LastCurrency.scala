package com.waynlaw.albabot.model

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
case class LastCurrency(
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
package com.waynlaw.albabot.model

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 18.
  * @note:
  */

/*
{
    "status"    : "0000",
    "order_id"  : "1429500241523",
    "data": [
        {
            "cont_id"   : "15364",
            "units"     : "0.16789964",
            "price"     : "270000",
            "total"     : 45333,
            "fee"       : "0.00016790"
        },
        {
            "cont_id"   : "15365",
            "units"     : "0.08210036",
            "price"     : "289000",
            "total"     : 23727,
            "fee"       : "0.00008210"
        }
    ]
}

 */
case class TradeResultData (contId: String, units: String, price: String, total: String, fee: String)

case class TradeResult (status: String, orderId: String, data: List[TradeResultData])


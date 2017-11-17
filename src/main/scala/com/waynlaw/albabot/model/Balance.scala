package com.waynlaw.albabot.model

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 17.
  * @note:
  */

/*
{
    "status"    : "0000",
    "data"      : {
        "total_btc"     : "665.40127447",
        "total_krw"     : "305507280",
        "in_use_btc"    : "127.43629364",
        "in_use_krw"    : "8839047.0000000000",
        "available_btc" : "537.96498083",
        "available_krw" : "294932685.000000000000",
        "xcoin_last"    : "505000"
    }
}
*/
case class BalanceData(
                        totalKrw: Int,
                        inUseKrw: Int,
                        availableKrw: Int,
                        misuKrw: Int,
                        misuDepoKrw: Int,
                        totalBtc: String,
                        inUseBtc: String,
                        availableBtc: String,
                        misuBtc: String,
                        xcoinLast: String
                      )

case class Balance(status: String, data: BalanceData)



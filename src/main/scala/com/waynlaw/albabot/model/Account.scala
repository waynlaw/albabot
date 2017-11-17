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
    "data"      : {
        "created"       : 1388118018000,
        "account_id"    : "A000105A",
        "trade_fee"     : "0.000",
        "balance"       : "665.40127447"
    }
}
 */
case class AccountData(
                 created: String,
                 accountId: String,
                 tradeFee: String,
                 balance: String
               )

case class Account(status: String, data: AccountData)


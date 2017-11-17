package com.waynlaw.albabot.model

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 17.
  * @note:
  */
case class RecentTransactionsData(
                 transactionDate: String,
                 `type`: String,
                 unitsTraded: String,
                 price: String,
                 total: String)

case class RecentTransactions(status: String, data: List[RecentTransactionsData])


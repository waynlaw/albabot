package com.waynlaw.albabot.model

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 18.
  * @note:
  */

/*
{
  "status": "0000",
  "data": {
    "wallet_address": "",
    "currency": "BTC"
  }
}

 */
case class WalletAddressData (walletAddress: String, currency: String)

case class WalletAddress (status: String, data: WalletAddressData)


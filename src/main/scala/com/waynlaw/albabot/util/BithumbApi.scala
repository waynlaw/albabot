package com.waynlaw.albabot.util

import com.waynlaw.albabot.Configure
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.util.EntityUtils
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpGet
import com.waynlaw.albabot.model._
import org.json4s.{DefaultFormats, JValue}
import org.json4s.native.JsonMethods.parse


/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 13.
  * @note: Bithumb Api Utils
  * @see: https://www.bithumb.com/u1/US127
  */
object BithumbApi {

  val config = Configure.load()

  val baseUrl: String = config.baseUrl

  implicit val formats = DefaultFormats

  val cli = new HttpClient(config.apiKey, config.secretKey)

  /**
    * API  : https://api.bithumb.com/public/ticker/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 마지막 거래 정보
    */
  def getTicker(currency: String): Ticker = {
    val api = List(baseUrl, "public/ticker", currency).mkString("/")
    cli.get(api).extract[Ticker]
  }

  /**
    * API  : https://api.bithumb.com/public/orderbook/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
    */
  def getOrderbook(currency: String): Orderbook = {
    val api = List(baseUrl, "public/orderbook", currency).mkString("/")
    cli.get(api).extract[Orderbook]
  }


  /**
    * API  : https://api.bithumb.com/public/recent_transactions/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 거래 체결 완료 내역
    */
  def getRecentTransactions(currency: String): RecentTransactions = {
    val api = List(baseUrl, "public/recent_transactions", currency).mkString("/")
    cli.get(api).extract[RecentTransactions]
  }

}

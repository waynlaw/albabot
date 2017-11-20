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
import org.json4s.{DefaultFormats, JValue, JsonAST}
import org.json4s.native.JsonMethods.parse


/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 13.
  * @note: Bithumb Api Utils
  * @see: https://www.bithumb.com/u1/US127
  */
object BithumbApi {

  implicit val formats = DefaultFormats

  val config = Configure.load()
  val baseUrl: String = config.baseUrl
  val client = new HttpClient(config.apiKey, config.secretKey)

  /**
    * API  : https://api.bithumb.com/public/ticker/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 마지막 거래 정보
    */
  def ticker(currency: String): Ticker = {
    val api = List(baseUrl, "public/ticker", currency).mkString("/")
    client.get(api).extract[Ticker]
  }

  /**
    * API  : https://api.bithumb.com/public/orderbook/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
    */
  def orderbook(currency: String): Orderbook = {
    val api = List(baseUrl, "public/orderbook", currency).mkString("/")
    client.get(api).extract[Orderbook]
  }


  /**
    * API  : https://api.bithumb.com/public/recent_transactions/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 거래 체결 완료 내역
    */
  def recentTransactions(currency: String): RecentTransactions = {
    val api = List(baseUrl, "public/recent_transactions", currency).mkString("/")
    client.get(api).extract[RecentTransactions]
  }

  /**
    * API  : https://api.bithumb.com/info/account
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 정보
    */
  def account(currency: String): Account = {
    val endpoint = "info/account"
    val api = List(baseUrl, endpoint).mkString("/")

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency)

    val nNonce = String.valueOf(System.currentTimeMillis)

    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> config.apiKey,
      "Api-Sign" -> HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey)),
      "Api-Nonce" -> nNonce
    )

    client.post(api, headers, params).extract[Account]
  }


  /**
    * API  : https://api.bithumb.com/info/balance
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 지갑 정보
    */
  def balance(currency: String): Balance = {
    val endpoint = "info/balance"
    val api = List(baseUrl, endpoint).mkString("/")

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency)

    val nNonce = String.valueOf(System.currentTimeMillis)

    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> config.apiKey,
      "Api-Sign" -> HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey)),
      "Api-Nonce" -> nNonce
    )

    client.post(api, headers, params).extract[Balance]
  }

  /**
    * API  : https://api.bithumb.com/info/wallet_address
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 입금 주소
    */
  def walletAddress(currency: String): WalletAddress = {
    val endpoint = "info/wallet_address"
    val api = List(baseUrl, endpoint).mkString("/")

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency)

    val nNonce = String.valueOf(System.currentTimeMillis)

    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> config.apiKey,
      "Api-Sign" -> HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey)),
      "Api-Nonce" -> nNonce
    )

    client.post(api, headers, params).extract[WalletAddress]
  }

  /**
    * API  : https://api.bithumb.com/trade/place
    * TYPE : Private
    * NOTE : bithumb 회원 판/구매 거래 주문 등록 및 체결
    */
  def place(currency: String, units: String, price: String, tradeType: String): Place = {
    val endpoint = "trade/place"
    val api = List(baseUrl, endpoint).mkString("/")

    val params = Map("endpoint" -> s"/$endpoint",
      "order_currency" -> currency,
      "units" -> units,
      "price" -> price,
      "type" -> tradeType
    )

    val nNonce = String.valueOf(System.currentTimeMillis)

    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> config.apiKey,
      "Api-Sign" -> HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey)),
      "Api-Nonce" -> nNonce
    )

    client.post(api, headers, params).extract[Place]
  }

  /**
    * API  : https://api.bithumb.com/info/order_detail
    * TYPE : Private
    * NOTE : bithumb 회원 판/구매 거래 주문 등록 및 체결
    */
  def orderDetail(currency: String, orderId: String, tradeType: String): OrderDetail = {
    val endpoint = "info/order_detail"
    val api = List(baseUrl, endpoint).mkString("/")

    val params = Map("endpoint" -> s"/$endpoint",
      "currency" -> currency,
      "order_id" -> orderId,
      "type" -> tradeType
    )

    val nNonce = String.valueOf(System.currentTimeMillis)

    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    val headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> config.apiKey,
      "Api-Sign" -> HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey)),
      "Api-Nonce" -> nNonce
    )

    client.post(api, headers, params).extract[OrderDetail]
  }
}

package com.waynlaw.albabot.util

import com.waynlaw.albabot.Configure
import com.waynlaw.albabot.model._
import com.waynlaw.albabot.model.coin.CoinType.Coin
import org.json4s.JsonAST.JString
import org.json4s.{DefaultFormats, JValue, JsonAST}


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
  def ticker(currency: Coin): Ticker = {
    val api = List(baseUrl, "public/ticker", currency.value).mkString("/")
    client.get(api).extract[Ticker]
  }

  /**
    * API  : https://api.bithumb.com/public/ticker/ALL
    * TYPE : Public
    * NOTE : bithumb 거래소 마지막 거래 정보
    */
  def tickerAll(): TickerAll = {
    val api = List(baseUrl, "public/ticker/ALL").mkString("/")
    val jValue: JsonAST.JValue = client.get(api)

    val status: String = (jValue \\ "status").extract[String]
    val date: String = (jValue \\ "data" \\ "date").extract[String]

    TickerAll(status,
      Map(
        "BTC" -> TickerData.from((jValue \\ "data" \\ "bTC").extract[TickerWithoutDate], date) ,
        "ETH" -> TickerData.from((jValue \\ "data" \\ "eTH").extract[TickerWithoutDate], date),
        "DASH" -> TickerData.from((jValue \\ "data" \\ "dASH").extract[TickerWithoutDate], date),
        "LTC" -> TickerData.from((jValue \\ "data" \\ "lTC").extract[TickerWithoutDate], date),
        "ETC" -> TickerData.from((jValue \\ "data" \\ "eTC").extract[TickerWithoutDate], date),
        "XRP" -> TickerData.from((jValue \\ "data" \\ "xRP").extract[TickerWithoutDate], date),
        "BCH" -> TickerData.from((jValue \\ "data" \\ "bCH").extract[TickerWithoutDate], date),
        "XMR" -> TickerData.from((jValue \\ "data" \\ "xMR").extract[TickerWithoutDate], date),
        "ZEC" -> TickerData.from((jValue \\ "data" \\ "zEC").extract[TickerWithoutDate], date),
        "QTUM" -> TickerData.from((jValue \\ "data" \\ "qTUM").extract[TickerWithoutDate], date)))
  }

  /**
    * API  : https://api.bithumb.com/public/orderbook/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
    */
  def orderbook(currency: Coin): Orderbook = {
    val api = List(baseUrl, "public/orderbook", currency.value).mkString("/")
    client.get(api).extract[Orderbook]
  }


  /**
    * API  : https://api.bithumb.com/public/recent_transactions/{currency}
    * TYPE : Public
    * NOTE : bithumb 거래소 거래 체결 완료 내역
    */
  def recentTransactions(currency: Coin): RecentTransactions = {
    val api = List(baseUrl, "public/recent_transactions", currency.value).mkString("/")
    client.get(api).extract[RecentTransactions]
  }

  /**
    * API  : https://api.bithumb.com/info/account
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 정보
    */
  def account(currency: Coin): Either[BithumbError, Account] = {
    val endpoint = "info/account"

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency.value)

    postCall[Account](endpoint, params)
  }


  /**
    * API  : https://api.bithumb.com/info/balance
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 지갑 정보
    */
  def balance(currency: Coin): Either[BithumbError, Balance] = {
    val endpoint = "info/balance"

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency.value)

    postCall[Balance](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/info/wallet_address
    * TYPE : Private
    * NOTE : bithumb 거래소 회원 입금 주소
    */
  def walletAddress(currency: Coin): Either[BithumbError, WalletAddress] = {
    val endpoint = "info/wallet_address"

    val params = Map("endpoint" -> s"/${endpoint}",
      "currency" -> currency.value)

    postCall[WalletAddress](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/info/orders
    * TYPE : Private
    * NOTE : 판/구매 거래 주문 등록 또는 진행 중인 거래
    */
  def orders(currency: Coin, orderId: String, `type`: String, count: String,  after: String): Either[BithumbError, Orders] = {
    val endpoint = "info/orders"

    val params = Map("endpoint" -> s"/$endpoint",
      "order_currency" -> currency.value,
      "currency" -> currency.value,
      "order_id" -> orderId,
      "type" -> `type`,
      "count" -> count,
      "after" -> after
    )

    postCall[Orders](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/trade/place
    * TYPE : Private
    * NOTE : bithumb 회원 판/구매 거래 주문 등록 및 체결
    */
  def place(currency: Coin, units: String, price: String, tradeType: String): Either[BithumbError, Place] = {
    val endpoint = "trade/place"

    val params = Map("endpoint" -> s"/$endpoint",
      "order_currency" -> currency.value,
      "units" -> units,
      "price" -> price,
      "type" -> tradeType
    )

    postCall[Place](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/info/order_detail
    * TYPE : Private
    * NOTE : bithumb 회원 판/구매 거래 주문 등록 및 체결
    */
  def orderDetail(currency: Coin, orderId: String, tradeType: String): Either[BithumbError, OrderDetail] = {
    val endpoint = "info/order_detail"

    val params = Map("endpoint" -> s"/$endpoint",
      "currency" -> currency.value,
      "order_id" -> orderId,
      "type" -> tradeType
    )

    postCall[OrderDetail](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/trade/market_buy
    * TYPE : Private
    * NOTE : 시장가 구매
    */
  def orderBuy(currency: Coin, units: String): Either[BithumbError, TradeResult] = {
    val endpoint = "trade/market_buy"

    val params = Map("endpoint" -> s"/$endpoint",
      "currency" -> currency.value,
      "units" -> units,
    )

    postCall[TradeResult](endpoint, params)
  }

  /**
    * API  : https://api.bithumb.com/trade/market_sell
    * TYPE : Private
    * NOTE : 시장가 구매
    */
  def orderSell(currency: Coin, units: String): Either[BithumbError, TradeResult] = {
    val endpoint = "trade/market_sell"

    val params = Map("endpoint" -> s"/$endpoint",
      "currency" -> currency.value,
      "units" -> units,
    )

    postCall[TradeResult](endpoint, params)
  }

  private def postCall[T: Manifest](endpoint: String, params: Map[String, String]): Either[BithumbError, T] = {
    val nNonce = makeNonce
    val apiSign = makeApiSign(endpoint, params, nNonce)
    val headers = makeHeaders(config.apiKey, apiSign, nNonce)
    parsingBody[T](client.post(List(baseUrl, endpoint).mkString("/"), headers, params))
  }

  private def makeNonce = String.valueOf(System.currentTimeMillis)

  private def makeApiSign(endpoint: String, params: Map[String, String], nNonce: String): String = {
    val strData = HtmlUtil.encodeURIComponent(HtmlUtil.mapToQueryString(params))
    val str: String = s"/${endpoint}" + ";" + strData + ";" + nNonce

    HtmlUtil.asHex(HtmlUtil.hmacSha512(str, config.secretKey))
  }

  private def makeHeaders(apiKey: String, apiSign: String, apiNonce: String): Map[String,String] = {
    Map(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=utf-8",
      "api-client-type" -> "2",
      "Api-Key" -> apiKey,
      "Api-Sign" -> apiSign,
      "Api-Nonce" -> apiNonce
    )
  }

  private def parsingBody[T: Manifest](body: JValue): Either[BithumbError, T] = {
    body \ "status" match {
      case JString("0000") =>
        Right(body.extract[T])
      case _ =>
        Left(body.extract[BithumbError])
    }
  }
}

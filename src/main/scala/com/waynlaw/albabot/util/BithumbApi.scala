package com.waynlaw.albabot.util

import com.waynlaw.albabot.Configure
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.util.EntityUtils
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpGet
import java.util.regex.Pattern

import com.waynlaw.albabot.model.LastCurrency
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

  def baseUrl: String = config.baseUrl

  /**
    * API  : https://api.bithumb.com/public/ticker/{currency}
    * NOTE : bithumb 거래소 마지막 거래 정보
    */
  def getLastInfo(currency: String): String = {
    val getLastInfoUrl = "public/ticker"
    val api = List(baseUrl, getLastInfoUrl, currency).mkString("/")


    val httpclient = HttpClients.createDefault

    val getRequest = new HttpGet(api)

    System.out.println("Executing request " + getRequest.getRequestLine)

    // Create a custom response handler
    val responseHandler = new ResponseHandler[String]() {
      override def handleResponse(response: HttpResponse): String = {
        val status: Int = response.getStatusLine.getStatusCode
        if (status >= 200 && status < 300) {
          val entity: HttpEntity = response.getEntity
          if (entity != null) EntityUtils.toString(entity)
          else null
        }
        else throw new ClientProtocolException("Unexpected response status: " + status)
      }
    }
    val responseBody: String = httpclient.execute(getRequest, responseHandler)

    Console println responseBody
    implicit val formats = DefaultFormats


    val parse1: JValue = parse(responseBody).camelizeKeys

    Console println parse1
    Console println (parse1 \\ "data").extract[LastCurrency]
    ""
  }
}

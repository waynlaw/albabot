package com.waynlaw.albabot.util

import org.apache.http.{HttpEntity, HttpResponse}
import org.apache.http.client.{ClientProtocolException, ResponseHandler}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.parse

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 17.
  * @note:
  */
class HttpClient(apiKey:String, secretKey: String) {

  val httpclient = HttpClients.createDefault

  implicit val formats = DefaultFormats

  // Create a custom response handler
  val resHandler = new ResponseHandler[String]() {
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

  def get(url: String): JValue ={
    val req = new HttpGet(url)
    val body: String = httpclient.execute(req, resHandler)
    parse(body).camelizeKeys
  }
}

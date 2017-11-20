package com.waynlaw.albabot.util

import org.apache.http.{Consts, HttpEntity, HttpResponse, NameValuePair}
import org.apache.http.client.{ClientProtocolException, ResponseHandler}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.parse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import java.util

/**
  *
  * @author: Lawrence
  * @since: 2017. 11. 17.
  * @note:
  */
class HttpClient(apiKey: String, secretKey: String) {

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

  def get(url: String): JValue = {
    val req = new HttpGet(url)
    val body: String = httpclient.execute(req, resHandler)
    parse(body).camelizeKeys
  }


  def post(url: String, headers: Map[String, String], params: Map[String, String]): JValue = {
    val formparams = new util.ArrayList[NameValuePair]
    for (p <- params) {
      formparams.add(new BasicNameValuePair(p._1, p._2))
    }
    val entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8)
    val req = new HttpPost(url)
    req.setEntity(entity)

    for (h <- headers) {
      req.setHeader(h._1, h._2)
    }

    val body: String = httpclient.execute(req, resHandler)

    Console println body
    parse(body).camelizeKeys
  }
}

package eu.walczakpawel

import org.apache.http._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList

import com.google.gson.Gson
import org.apache.http.entity.StringEntity
import eu.walczakpawel.model.Campaign

case class Campaigns(campaigns: Array[Campaign])

package object qldb {

  def putTransaction(campaignsList: List[Campaign]): Unit = {
    // create our object as a json string
    val campaigns = new Campaigns(campaignsList.toArray)
    val spockAsJson = new Gson().toJson(campaigns)

    // add name value pairs to a post object
    val post = new HttpPost("http://localhost:3000/qldb/insertDocument")
    post.setEntity(new StringEntity(spockAsJson))
    post.setHeader("Content-type", "application/json")
    println(spockAsJson)
    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
    println("--- HEADERS ---")
    response.getAllHeaders.foreach(arg => println(arg))
  }


}
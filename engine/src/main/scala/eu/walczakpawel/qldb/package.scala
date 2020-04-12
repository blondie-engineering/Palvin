package eu.walczakpawel
import requests.post

package object qldb {

  def putTransaction(company: String, amount: Integer): Unit = {
    val r = requests.post("http://localhost:3000/qldb/insertTransaction", data = Map("company" -> company, "amount" -> amount.toString));

  }

}

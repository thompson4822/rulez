package com.minutekey

import java.sql.Timestamp

/**
 * Created by steve on 7/9/14.
 */

// Create a few domain models here that we want to populate with information from the logs
case class Screen(name: String, time: Timestamp, session: String)

case class Key(product: String, cost: BigDecimal, lastMinuteOffer: Boolean)

case class Cart(keys: List[Key])

case class CustomerTransaction(session: String, screens: List[Screen], cart: Cart, paymentMethod: String)

// Create parser
object ModelParser {

}

object Main {
  def main(args: Array[String]) {
    println("Yeah, it looks like its working")
  }
}


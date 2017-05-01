package dk.diku.blockchain

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.language.postfixOps

class Node(port: Int) {

  val system = ActorSystem("bc-system")
  val chainActor = system.actorOf(Props[ChainActor], "chain")
  implicit val duration: Timeout = 20 seconds

  def getLength() = {
    val f = chainActor ? GetLength
    val result = Await.ready(f, 20 seconds).value.get
    result match {
      case Success(length) => Some(length)
      case Failure(t) => None
    }
  }

}

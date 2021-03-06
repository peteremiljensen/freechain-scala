package dk.diku.freechain

import org.json4s._
import org.json4s.native.JsonMethods._
import java.text.SimpleDateFormat
import java.util.Calendar
import com.roundeights.hasher.Implicits._

case class Block(loaves: Seq[Loaf], height: Int,
  previousBlockHash: String, timestamp: String,
  data: JValue, hash: String)(implicit validator: Validator) {

  lazy val calculateHash: String = {
    val strippedJson = compact(render(Loaf.sortJson(JObject(
      toJson.obj.filter(_._1 != "hash")
    ))))
    strippedJson.toString.sha256
  }

  lazy val validate: Boolean =
    (loaves.foldLeft(true) ((and, l) => and && l.validate)) &&
    validator.block(this)

  lazy val toJson = JObject(
    "loaves" -> JArray(loaves.map(l => l.toJson).toList),
    "height" -> JInt(height),
    "previous_block_hash" -> JString(previousBlockHash),
    "timestamp" -> JString(timestamp),
    "data" -> data,
    "hash" -> JString(hash)
  )
}

object Block {

  def generateBlock(loaves: Seq[Loaf], previousBlock: Block, data: JValue)
    (implicit validator: Validator) = {
    val timestamp: String = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").
      format(Calendar.getInstance().getTime())
    val hash = new Block(loaves, previousBlock.height+1,
      previousBlock.hash, timestamp, data, "").calculateHash
    new Block(loaves, previousBlock.height+1,
      previousBlock.hash, timestamp, data, hash)
  }
}

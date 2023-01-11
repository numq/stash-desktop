package extension

import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import websocket.Message

val String.isSocketMessage: Boolean
    get() = runCatching {
        with(JSONObject(this)) {
            has(Message.TYPE) && has(Message.BODY)
        }
    }.isSuccess

val String.message: Message
    get() = with(JSONObject(this)) {
        Message(getString(Message.TYPE), JSONObject(getString(Message.BODY)))
    }

fun String.decodeBase64(): ByteArray? = try {
    Base64.decodeBase64(this)
} catch (e: Exception) {
    println(e.localizedMessage)
    null
}

package extension

import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import websocket.WebSocketConstants
import websocket.WebSocketMessage

val String.isSocketMessage: Boolean
    get() = runCatching {
        with(JSONObject(this)) {
            has(WebSocketConstants.TYPE) && has(WebSocketConstants.BODY)
        }
    }.isSuccess

val String.webSocketMessage: WebSocketMessage
    get() = with(JSONObject(this)) {
        WebSocketMessage(getString(WebSocketConstants.TYPE), JSONObject(getString(WebSocketConstants.BODY)))
    }

fun String.decodeBase64(): ByteArray? = try {
    Base64.decodeBase64(this)
} catch (e: Exception) {
    println(e.localizedMessage)
    null
}

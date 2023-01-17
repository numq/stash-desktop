package extension

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

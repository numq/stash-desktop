package extension

import org.java_websocket.util.Base64
import java.io.File

val File.base64: String
    get() = Base64.encodeBytes(readBytes())
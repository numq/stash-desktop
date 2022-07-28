package extension

import files.ImageFile
import websocket.WebSocketConstants
import websocket.WebSocketMessage

val WebSocketMessage.isImageFile: Boolean
    get() = runCatching {
        body.has(WebSocketConstants.FILE_EXTENSION) && body.has(WebSocketConstants.FILE_BLOB)
    }.isSuccess

val WebSocketMessage.imageFile: ImageFile
    get() = with(body) {
        ImageFile(
            getString(WebSocketConstants.FILE_EXTENSION),
            getString(WebSocketConstants.FILE_BLOB)
        )
    }
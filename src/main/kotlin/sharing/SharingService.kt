package sharing

import extension.imageFile
import extension.isImageFile
import files.FileEvent
import files.ImageFile
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import websocket.WebSocketApi
import websocket.WebSocketConstants
import websocket.WebSocketMessage

class SharingService(
    private val socketClient: WebSocketApi.Client,
    private val socketServer: WebSocketApi.Server
) : SharingApi {

    override val events = socketClient.messages.consumeAsFlow().map {
        when (it.type) {
            WebSocketConstants.CLEAR -> FileEvent.Clear
            WebSocketConstants.REFRESH -> FileEvent.Refresh
            WebSocketConstants.FILE -> if (it.isImageFile) FileEvent.File(it.imageFile) else FileEvent.Empty
            else -> FileEvent.Empty
        }
    }

    override fun clear() = socketClient.signal(WebSocketMessage(WebSocketConstants.CLEAR))

    override fun refresh() = socketClient.signal(WebSocketMessage(WebSocketConstants.REFRESH))

    override fun startSharing() = socketServer.start() && socketClient.connect()

    override fun stopSharing() = !(socketClient.disconnect() && socketServer.stop())

    override fun shareFile(file: ImageFile) =
        socketClient.signal(WebSocketMessage(WebSocketConstants.FILE, JSONObject().apply {
            put(WebSocketConstants.FILE_EXTENSION, file.extension)
            put(WebSocketConstants.FILE_BLOB, file.blob)
        }))
}
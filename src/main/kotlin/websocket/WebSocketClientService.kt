package websocket

import extension.isSocketMessage
import extension.webSocketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.DatagramSocket
import java.net.DatagramSocketImpl
import java.net.DatagramSocketImplFactory
import java.net.URI
import java.nio.ByteBuffer

class WebSocketClientService : WebSocketApi.Client {

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    private val uri = URI("ws://${WebSocketConfig.DEFAULT_HOST}:${WebSocketConfig.DEFAULT_PORT}")
    private var webSocketClient: WebSocketClient? = null

    private fun createClient() = object : WebSocketClient(uri) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            println("Connected to server")
        }

        override fun onMessage(message: String?) {
            message?.let {
                println("Got message from server: ${it.take(50)}")
                if (it.isSocketMessage) {
                    coroutineScope.launch {
                        messages.send(it.webSocketMessage)
                    }
                }
            }
        }

        override fun onMessage(bytes: ByteBuffer?) {
            super.onMessage(bytes)
            bytes?.array()?.toString()?.let {
                println("Got message from server: ${it.take(50)}")
                if (it.isSocketMessage) {
                    coroutineScope.launch {
                        messages.send(it.webSocketMessage)
                    }
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("Disconnected from server")
        }

        override fun onError(ex: Exception?) {

        }
    }

    override val messages: Channel<WebSocketMessage> = Channel()

    override fun signal(message: WebSocketMessage) = runCatching {
        webSocketClient?.send(message.toString())
    }.isSuccess

    override fun connect() = runCatching {
        webSocketClient = createClient()
        webSocketClient?.connect()
    }.isSuccess

    override fun disconnect() = runCatching {
        webSocketClient?.close()
        webSocketClient = null
    }.isSuccess
}
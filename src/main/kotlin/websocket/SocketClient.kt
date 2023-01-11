package websocket

import extension.isSocketMessage
import extension.message
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class SocketClient constructor(
    private val address: String
) : SocketService.Client {

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    private var webSocketClient: WebSocketClient? = null

    private fun createClient() = object : WebSocketClient(URI(address)) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            println("Connected to server")
        }

        override fun onMessage(message: String?) {
            message?.takeIf { it.isSocketMessage }?.let {
                println("Got message from server: ${it.take(50)}")
                coroutineScope.launch {
                    messages.send(it.message)
                }
            }
        }

        override fun onMessage(bytes: ByteBuffer?) {
            bytes?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                println("Got message from server: ${it.take(50)}")
                coroutineScope.launch {
                    messages.send(it.message)
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("Disconnected from server")
        }

        override fun onError(e: Exception?) {
            println("Client exception: ${e?.localizedMessage ?: "Something went wrong"}")
        }
    }

    override val messages: Channel<Message> = Channel()

    override fun signal(message: Message) = runCatching {
        webSocketClient?.send(message.toString())
    }.fold(onSuccess = { Right(Unit) }, onFailure = { Left(Exception(it)) })

    override fun connect() = runCatching {
        webSocketClient = createClient()
        webSocketClient?.connect()
    }.fold(onSuccess = { Right(Unit) }, onFailure = { Left(Exception(it)) })

    override fun disconnect() = runCatching {
        webSocketClient?.close()
        webSocketClient = null
    }.fold(onSuccess = { Right(Unit) }, onFailure = { Left(Exception(it)) })
}
package websocket

import extension.isSocketMessage
import extension.message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class SocketClient constructor(
    private val address: String
) : SocketService.Client {

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    private var client: WebSocketClient? = null

    private fun createClient() = object : WebSocketClient(URI(address)) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            println("Connected to server")
            _connectionState.update { ConnectionState.CONNECTED }
        }

        override fun onMessage(message: String?) {
            message?.takeIf { it.isSocketMessage }?.let {
                println("Got message from server: ${it.take(50)}")
                messages.trySend(it.message)
            }
        }

        override fun onMessage(bytes: ByteBuffer?) {
            bytes?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                println("Got message from server: ${it.take(50)}")
                messages.trySend(it.message)
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            _connectionState.update { ConnectionState.DISCONNECTED }
            println("Disconnected from server")
            if (code != 1000) start()
        }

        override fun onError(e: Exception?) {
            println("Client exception: ${e?.localizedMessage ?: "Socket error"}")
        }

        override fun reconnect() {
            println("Client reconnecting")
        }
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override val messages: Channel<Message> = Channel(Channel.UNLIMITED)

    override suspend fun signal(message: Message) {
        client?.run { if (isOpen) send(message.toString()) }
    }

    override fun start() {
        if (client != null) stop()
        _connectionState.update { ConnectionState.CONNECTING }
        client = createClient()
        coroutineScope.launch {
            delay(1000)
            try {
                client?.connect()
            } catch (e: Exception) {
                client?.reconnect()
            }
        }
    }

    override fun stop() {
        client?.close(1000)
        client = null
        _connectionState.update { ConnectionState.DISCONNECTED }
    }
}
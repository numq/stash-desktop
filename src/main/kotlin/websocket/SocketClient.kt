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


class SocketClient : SocketService.Client {

    private var connectionJob: Job? = null
    private var client: WebSocketClient? = null

    private fun createClient(address: SocketAddress, onSuccess: () -> Unit) =
        object : WebSocketClient(URI.create(address.toString())) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                println("Connected to server")
                onSuccess()
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
                println("Disconnected from server")
                _connectionState.update { ConnectionState.Disconnected }
                if (code != 1000) startWithAddress(address)
            }

            override fun onError(e: Exception?) {
                println("Client exception: ${e?.localizedMessage ?: "Socket error"}")
            }

            override fun reconnect() {
                println("Client reconnecting")
            }
        }

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override val messages: Channel<Message> = Channel(Channel.UNLIMITED)

    override suspend fun signal(message: Message) {
        client?.run { if (isOpen) send(message.toString()) }
    }

    private fun connectToClient(address: SocketAddress, callback: () -> Unit = {}) {
        if (client != null) stop()
        _connectionState.update { ConnectionState.Connecting }
        connectionJob = CoroutineScope(Dispatchers.Default + Job()).launch {
            client = createClient(address) { callback() }
            delay(1000L)
            client?.connect()
        }
    }

    override fun startWithString(address: String?) {
        val socketAddress = address?.let { addr ->
            Regex(SocketService.REGEX_PATTERN)
                .matchEntire(addr)
                ?.groups
                ?.filterNotNull()
                ?.drop(1)
                ?.takeIf { it.size == 3 }
                ?.runCatching {
                    val (protocol, hostname, port) = map { it.value }.toTypedArray()
                    SocketAddress(protocol, hostname, port.toInt())
                }
                ?.getOrNull()
        } ?: SocketAddress()
        connectToClient(socketAddress) {
            _connectionState.update { ConnectionState.Connected(false, socketAddress) }
        }
    }

    override fun startWithAddress(address: SocketAddress) {
        connectToClient(address) {
            _connectionState.update { ConnectionState.Connected(true, address) }
        }
    }

    override fun stop() {
        connectionJob?.cancel()
        client?.close(1000)
        client = null
        _connectionState.update { ConnectionState.Disconnected }
    }
}
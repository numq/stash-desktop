package websocket

import extension.isSocketMessage
import extension.message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.InetAddress
import java.net.URI
import java.nio.ByteBuffer
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener


class SocketClient : SocketService.Client {

    private var connectionJob: Job? = null
    private var client: WebSocketClient? = null
    private var jmDNS: JmDNS? = null

    private val _hostname = MutableStateFlow<String?>(null)
    private val hostname: StateFlow<String?> = _hostname

    private val dnsListener = object : ServiceListener {
        override fun serviceAdded(event: ServiceEvent) {
            println("Service added")
        }

        override fun serviceRemoved(event: ServiceEvent) {
            println("Service removed")
        }

        override fun serviceResolved(event: ServiceEvent) {
            println("Service resolved")
            if (event.name == SocketService.SERVICE_NAME && event.type == SocketService.SERVICE_TYPE) {
                _hostname.update { event.info.getPropertyString("hostname") }
            }
        }
    }

    private fun createClient(hostname: String) =
        object : WebSocketClient(URI(SocketService.ADDRESS_PATTERN.format(hostname, SocketService.SERVICE_PORT))) {
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
        connectionJob = CoroutineScope(Dispatchers.Default).launch {
            jmDNS = JmDNS.create(InetAddress.getLocalHost(), SocketService.SERVICE_NAME).apply {
                addServiceListener(SocketService.SERVICE_TYPE, dnsListener)
            }



            hostname.filterNotNull().collect {
                client?.apply { delay(1000L) }?.close()
                client = null
                client = createClient(it).apply {
                    connect()
                }
                return@collect
            }
        }
    }

    override fun stop() {
        connectionJob?.cancel()
        client?.close(1000)
        _connectionState.update { ConnectionState.DISCONNECTED }
    }
}
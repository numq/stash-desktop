package websocket

import extension.isSocketMessage
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

class SocketServer : SocketService.Server {

    private var server: WebSocketServer? = null
    private var jmDNS: JmDNS? = null

    private val hostname = NetworkInterface.getNetworkInterfaces().toList().flatMap { networkInterface ->
        networkInterface.inetAddresses.toList()
    }.firstOrNull { !it.isLoopbackAddress }?.hostAddress ?: Inet4Address.getLocalHost().hostAddress

    private val serviceInfo = ServiceInfo.create(
        SocketService.SERVICE_TYPE,
        SocketService.SERVICE_NAME,
        SocketService.SERVICE_PORT,
        1,
        1,
        false,
        mapOf("hostname" to hostname)
    )

    private fun createServer(address: InetSocketAddress) = runCatching {
        object : WebSocketServer(address) {
            override fun onStart() {
                println("Server started on port: $port")
            }

            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                println("Client connected")
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                println("Client disconnected")
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                message?.takeIf { it.isSocketMessage }?.let {
                    println(it.take(100))
                    broadcast(it)
                }
            }

            override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
                message?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                    println(it.take(100))
                    broadcast(it)
                }
            }

            override fun onError(conn: WebSocket?, e: Exception?) {
                println("Server exception: ${e?.localizedMessage ?: "Something went wrong"}")
            }
        }
    }.getOrNull()

    override suspend fun start(): Boolean {
        if (server != null) stop()
        server = createServer(InetSocketAddress(hostname, SocketService.SERVICE_PORT))
        server?.run {
            if (jmDNS == null) {
                jmDNS = JmDNS.create().apply {
                    registerService(serviceInfo)
                }
            }
            start()
        }
        return server != null
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}
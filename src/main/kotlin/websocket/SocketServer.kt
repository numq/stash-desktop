package websocket

import extension.isSocketMessage
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SocketServer : SocketService.Server {

    private var server: WebSocketServer? = null

    private val inetAddress = (NetworkInterface.getNetworkInterfaces().toList().flatMap { networkInterface ->
        networkInterface.inetAddresses.toList()
    }.firstOrNull { !it.isLoopbackAddress } ?: InetAddress.getLocalHost())

    private fun createServer(
        address: InetSocketAddress,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ): WebSocketServer? {
        return try {
            object : WebSocketServer(address) {
                override fun onStart() {
                    println("Server started on port: $port")
                    onSuccess()
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
                    e?.let { onError(it) }
                }
            }
        } catch (e: Exception) {
            println("Failed to create WebSocket server: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun start() = suspendCoroutine { continuation ->
        if (server != null) stop()
        val address = SocketAddress(hostname = inetAddress.hostAddress)
        server = createServer(InetSocketAddress(address.hostname, address.port), onSuccess = {
            continuation.resume(address)
        }, onError = { continuation.resume(null) })
        server?.start()
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}
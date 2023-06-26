package websocket

import extension.isSocketMessage
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SocketServer : SocketService.Server {

    private var server: WebSocketServer? = null

    private val defaultAddress =
        (NetworkInterface.getNetworkInterfaces().toList().flatMap { networkInterface ->
            networkInterface.inetAddresses.toList()
        }.lastOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?: InetAddress.getLocalHost()).run { SocketAddress(hostname = hostAddress) }

    private fun createServer(
        address: SocketAddress,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ): WebSocketServer {
        return object : WebSocketServer(InetSocketAddress(address.hostname, address.port)) {
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
                e?.let(onError)
            }
        }
    }

    override suspend fun start(port: Int?): SocketAddress? = suspendCoroutine { continuation ->
        if (server != null) stop()
        val address = port?.let { defaultAddress.copy(port = port) } ?: defaultAddress
        server = createServer(address, onSuccess = {
            continuation.resume(address)
        }, onError = {
            continuation.resume(null)
        })
        server?.start()
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}
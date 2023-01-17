package websocket

import extension.isSocketMessage
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class SocketServer constructor(
    private val hostname: String,
    private val port: Int,
) : SocketService.Server {

    private var server: WebSocketServer? = null

    private fun createServer(onServerStarted: () -> Unit) =
        object : WebSocketServer(InetSocketAddress(hostname, port)) {
            override fun onStart() {
                println("Server started on port: $port")
                onServerStarted()
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

    override fun start(onServerStarted: () -> Unit) {
        if (server != null) stop()
        server = createServer(onServerStarted)
        server?.start()
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}
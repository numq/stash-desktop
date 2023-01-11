package websocket

import extension.isSocketMessage
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*

class SocketServer constructor(
    private val hostname: String,
    private val port: Int,
) : SocketService.Server {

    private fun createServer() = object : WebSocketServer(InetSocketAddress(hostname, port)) {
        override fun onStart() {
            println("Server started on port: $port")
        }

        override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
            sessions += conn
            println("Client connected")
        }

        override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
            sessions -= conn
            println("Client disconnected")
        }

        override fun onMessage(conn: WebSocket?, message: String?) {
            message?.takeIf { it.isSocketMessage }?.let {
                println(it.take(100))
                server?.broadcast(it)
//                conn?.send(it)
//                sessions.filterNot { session -> session == conn }.forEach { session ->
//                    session.send(it)
//                }
            }
        }

        override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
            message?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                println(it.take(100))
                server?.broadcast(it)
//                conn?.send(it)
//                sessions.filterNot { session -> session == conn }.forEach { session ->
//                    session.send(it)
//                }
            }
        }

        override fun onError(conn: WebSocket?, e: Exception?) {
            println("Client exception: ${e?.localizedMessage ?: "Something went wrong"}")
        }
    }

    private var server: WebSocketServer? = null
    private val sessions = Collections.synchronizedSet<WebSocket>(LinkedHashSet())

    override fun start() = runCatching {
        server = createServer()
        server?.start()
    }.fold(onSuccess = { Right(Unit) }, onFailure = { Left(Exception(it)) })

    override fun stop() = runCatching {
        server?.stop()
        server = null
    }.fold(onSuccess = { Right(Unit) }, onFailure = { Left(Exception(it)) })
}
package websocket

import extension.isSocketMessage
import kotlinx.coroutines.suspendCancellableCoroutine
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SocketServer constructor(
    private val hostname: String,
    private val port: Int,
) : SocketService.Server {

    private var server: WebSocketServer? = null

    private fun createServer(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) =
        object : WebSocketServer(InetSocketAddress(hostname, port)) {
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
                e?.cause?.let(onFailure)
            }
        }

    override suspend fun start() = suspendCancellableCoroutine { continuation ->
        if (server != null) stop()
        server = createServer(onSuccess = {
            continuation.resume(true)
        }, onFailure = continuation::resumeWithException)
        server?.start()
        continuation.invokeOnCancellation {
            server?.stop()
            server = null
        }
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}
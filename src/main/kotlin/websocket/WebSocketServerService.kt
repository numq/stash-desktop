package websocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*

class WebSocketServerService : WebSocketApi.Server {

    private val coroutineContext = Dispatchers.IO + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    private fun createServer() = object : WebSocketServer(
        InetSocketAddress(
            WebSocketConfig.DEFAULT_HOST, WebSocketConfig.DEFAULT_PORT
        )
    ) {
        override fun onStart() {
            println("onStart")
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
            message?.let {
                println(it.take(100))
                coroutineScope.launch {
                    sessions.forEach { session ->
                        session.send(it)
                    }
                }
            }
        }

        override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
            super.onMessage(conn, message)
            message?.array()?.toString()?.let {
                println(it.take(100))
                coroutineScope.launch {
                    sessions.forEach { session ->
                        session.send(it)
                    }
                }
            }
        }

        override fun onError(conn: WebSocket?, ex: Exception?) {
            ex?.let { println("OnError: ${ex.localizedMessage}") }
        }
    }

    private var server: WebSocketServer? = null
    private val sessions = Collections.synchronizedSet<WebSocket>(LinkedHashSet())

    override fun start() = runCatching {
        server = createServer()
        server?.start()
    }.isSuccess

    override fun stop() = runCatching {
        server?.stop()
        server = null
    }.isSuccess
}
package websocket

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface SocketService {
    companion object {
        const val SERVICE_NAME = "stash"
        const val SERVICE_TYPE = "_ws._tcp."
        const val SERVICE_PORT = 9000
        const val ADDRESS_PATTERN = "ws://%s:%s"
    }

    interface Client {
        val connectionState: StateFlow<ConnectionState>
        val messages: Channel<Message>
        suspend fun signal(message: Message)
        fun start()
        fun stop()
    }

    interface Server {
        suspend fun start(): Boolean
        fun stop()
    }
}
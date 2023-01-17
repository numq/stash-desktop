package websocket

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface SocketService {
    companion object {
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
        fun start(onServerStarted: () -> Unit)
        fun stop()
    }
}
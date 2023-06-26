package websocket

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface SocketService {
    companion object {
        const val REGEX_PATTERN = "(ws{1,2}):\\/\\/(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})"
        const val DEFAULT_HOSTNAME = "127.0.0.1"
        const val DEFAULT_PORT = 9000
    }

    interface Client {
        val connectionState: StateFlow<ConnectionState>
        val messages: Channel<Message>
        suspend fun signal(message: Message)
        fun startWithString(address: String?)
        fun startWithAddress(address: SocketAddress)
        fun stop()
    }

    interface Server {
        suspend fun start(port: Int?): SocketAddress?
        fun stop()
    }
}
package websocket

import kotlinx.coroutines.channels.Channel

interface WebSocketApi {
    interface Client {
        val messages: Channel<WebSocketMessage>
        fun signal(message: WebSocketMessage): Boolean
        fun connect(): Boolean
        fun disconnect(): Boolean
    }

    interface Server {
        fun start(): Boolean
        fun stop(): Boolean
    }
}
package websocket

import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.channels.Channel

interface SocketService {
    companion object {
        const val ADDRESS_PATTERN = "ws://%s:%s"
    }

    interface Client {
        val messages: Channel<Message>
        fun signal(message: Message): Either<Exception, Unit>
        fun connect(): Either<Exception, Unit>
        fun disconnect(): Either<Exception, Unit>
    }

    interface Server {
        fun start(): Either<Exception, Unit>
        fun stop(): Either<Exception, Unit>
    }
}
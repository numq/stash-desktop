package file

import action.ActionStatus
import extension.file
import extension.isFile
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import websocket.Message
import websocket.SocketService

interface FileService {

    val events: Either<Exception, Flow<FileEvent>>
    suspend fun clear(): Either<Exception, ActionStatus>
    suspend fun refresh(): Either<Exception, ActionStatus>
    suspend fun sendFile(name: String, extension: String, bytes: ByteArray): Either<Exception, ActionStatus>

    class Implementation constructor(
        private val client: SocketService.Client
    ) : FileService {

        override val events = runCatching {
            client.messages.consumeAsFlow().map {
                when (it.type) {
                    Message.CLEAR -> FileEvent.Clear
                    Message.REFRESH -> FileEvent.Refresh
                    Message.FILE -> it.takeIf { it.isFile }?.let { message -> FileEvent.File(message.file) }
                        ?: FileEvent.Empty
                    else -> FileEvent.Empty
                }
            }
        }.fold(onSuccess = { Right(it) }, onFailure = { Left(Exception(it)) })

        override suspend fun clear() =
            Right(ActionStatus.CANCELED).flatMap { client.signal(Message(Message.CLEAR)) }.map { ActionStatus.DONE }

        override suspend fun refresh() =
            Right(ActionStatus.CANCELED).flatMap { client.signal(Message(Message.REFRESH)) }.map { ActionStatus.DONE }

        override suspend fun sendFile(name: String, extension: String, bytes: ByteArray) =
            Right(ActionStatus.CANCELED).flatMap {
                client.signal(Message(Message.FILE, JSONObject().apply {
                    put(File.NAME, name)
                    put(File.EXTENSION, extension)
                    put(File.BYTES, Base64.encodeBase64String(bytes))
                }))
            }.map { ActionStatus.DONE }
    }
}
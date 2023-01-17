package file

import extension.file
import extension.isFile
import extension.toEither
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import websocket.Message
import websocket.SocketService

interface FileRepository {

    val events: Either<Exception, Flow<FileEvent>>
    suspend fun refreshFiles(): Either<Exception, Unit>
    suspend fun shareFile(
        name: String,
        extension: String,
        bytes: ByteArray
    ): Either<Exception, Unit>

    suspend fun removeFile(file: File): Either<Exception, Unit>

    class Implementation constructor(
        private val client: SocketService.Client
    ) : FileRepository {

        override val events = runCatching {
            client.messages.consumeAsFlow().map {
                when (it.type) {
                    Message.REFRESH -> FileEvent.Refresh
                    Message.UPLOAD -> it.takeIf { it.isFile }?.let { message -> FileEvent.Upload(message.file) }
                        ?: FileEvent.Empty
                    Message.DELETE -> it.takeIf { it.isFile }?.let { message -> FileEvent.Delete(message.file) }
                        ?: FileEvent.Empty
                    else -> FileEvent.Empty
                }
            }
        }.toEither()

        override suspend fun refreshFiles() = runCatching {
            client.signal(Message(Message.REFRESH))
        }.toEither()

        override suspend fun shareFile(name: String, extension: String, bytes: ByteArray) =
            runCatching {
                client.signal(Message(Message.UPLOAD, JSONObject().apply {
                    put(File.NAME, name)
                    put(File.EXTENSION, extension)
                    put(File.BYTES, Base64.encodeBase64String(bytes))
                }))
            }.toEither()

        override suspend fun removeFile(file: File) = runCatching {
            client.signal(Message(Message.DELETE, JSONObject().apply {
                put(File.NAME, file.name)
                put(File.EXTENSION, file.extension)
                put(File.BYTES, Base64.encodeBase64String(file.bytes))
            }))
        }.toEither()

    }
}
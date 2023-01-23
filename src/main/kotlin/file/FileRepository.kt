package file

import extension.catch
import extension.catchAsync
import extension.file
import extension.isFile
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

        override val events = catch {
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
        }

        override suspend fun refreshFiles() = catchAsync {
            client.signal(Message(Message.REFRESH))
        }

        override suspend fun shareFile(name: String, extension: String, bytes: ByteArray) = catchAsync {
            client.signal(Message(Message.UPLOAD, JSONObject().apply {
                put(File.NAME, name)
                put(File.EXTENSION, extension)
                put(File.BYTES, Base64.encodeBase64String(bytes))
            }))
        }

        override suspend fun removeFile(file: File) = catchAsync {
            client.signal(Message(Message.DELETE, JSONObject().apply {
                put(File.NAME, file.name)
                put(File.EXTENSION, file.extension)
                put(File.BYTES, Base64.encodeBase64String(file.bytes))
            }))
        }
    }
}
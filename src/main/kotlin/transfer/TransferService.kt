package transfer

import extension.catch
import extension.catchAsync
import file.File
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createFile
import kotlin.io.path.outputStream
import kotlin.io.path.writeBytes

interface TransferService {

    val actions: Either<Exception, Channel<TransferAction>>
    suspend fun requestTransfer(event: TransferAction): Either<Exception, Unit>
    suspend fun downloadFile(path: String, name: String, bytes: ByteArray): Either<Exception, Unit>
    suspend fun downloadZip(path: String, name: String?, files: List<File>): Either<Exception, Unit>

    class Implementation : TransferService {

        override val actions = catch {
            Channel<TransferAction>(Channel.UNLIMITED)
        }

        override suspend fun requestTransfer(event: TransferAction) = catch {
            actions.getOrNull()?.trySend(event)?.getOrNull() ?: Unit
        }

        override suspend fun downloadFile(
            path: String,
            name: String,
            bytes: ByteArray
        ) = catchAsync(Dispatchers.IO) {
            val uri = "$path/$name"
            val file = Paths.get(uri)
            file.writeBytes(bytes)
            file.createFile()
            Unit
        }

        override suspend fun downloadZip(
            path: String,
            name: String?,
            files: List<File>
        ) = catchAsync(Dispatchers.IO) {
            val uri = "$path/$name"
            val file = Paths.get(uri)
            file.outputStream().use {
                ZipOutputStream(it).use { zip ->
                    files.forEach { file ->
                        zip.putNextEntry(ZipEntry("${file.name}.${file.extension}"))
                        zip.write(file.bytes)
                    }
                }
            }
            file.createFile()
            Unit
        }
    }
}
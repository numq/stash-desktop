package transfer

import extension.catch
import extension.catchAsync
import file.File
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface TransferService {

    val actions: Either<Exception, Channel<TransferAction>>
    suspend fun requestTransfer(event: TransferAction): Either<Exception, Unit>
    suspend fun downloadFile(path: String, name: String, extension: String, bytes: ByteArray): Either<Exception, Unit>
    suspend fun downloadZip(path: String, name: String?, files: List<File>): Either<Exception, Unit>

    class Implementation : TransferService {

        override val actions = catch {
            Channel<TransferAction>(Channel.UNLIMITED)
        }

        override suspend fun requestTransfer(event: TransferAction) = catch {
            actions.getOrNull()?.trySend(event)?.getOrThrow() ?: Unit
        }

        override suspend fun downloadFile(
            path: String, name: String,
            extension: String,
            bytes: ByteArray
        ) = catchAsync(Dispatchers.IO) {
            FileOutputStream("$path$name.$extension").use {
                it.write(bytes)
            }
        }

        override suspend fun downloadZip(
            path: String,
            name: String?,
            files: List<File>
        ) = catchAsync(Dispatchers.IO) {
            ZipOutputStream(FileOutputStream("$path$name.zip")).use { zip ->
                files.forEach { file ->
                    zip.putNextEntry(ZipEntry("${file.name}.${file.extension}"))
                    zip.write(file.bytes)
                }
            }
        }
    }
}
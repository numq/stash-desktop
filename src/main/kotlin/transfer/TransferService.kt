package transfer

import extension.toEither
import file.File
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface TransferService {

    val actions: Either<Exception, Channel<TransferAction>>
    suspend fun requestTransfer(event: TransferAction): Either<Exception, Unit>
    suspend fun downloadFile(path: String, name: String, extension: String, bytes: ByteArray): Either<Exception, Unit>
    suspend fun downloadZip(path: String, name: String?, files: List<File>): Either<Exception, Unit>

    class Implementation : TransferService {

        override val actions = runCatching {
            Channel<TransferAction>(Channel.UNLIMITED)
        }.toEither()

        override suspend fun requestTransfer(event: TransferAction) = runCatching {
            actions.getOrNull()?.trySend(event)?.getOrThrow() ?: Unit
        }.toEither()

        override suspend fun downloadFile(
            path: String, name: String,
            extension: String,
            bytes: ByteArray
        ) = runCatching {
            withContext(Dispatchers.IO) {
                FileOutputStream("$path$name.$extension").use {
                    it.write(bytes)
                }
            }
        }.toEither()

        override suspend fun downloadZip(
            path: String,
            name: String?,
            files: List<File>
        ) = runCatching {
            withContext(Dispatchers.IO) {
                ZipOutputStream(FileOutputStream("$path$name.zip")).use { zip ->
                    files.forEach { file ->
                        zip.putNextEntry(ZipEntry("${file.name}.${file.extension}"))
                        zip.write(file.bytes)
                    }
                }
            }
        }.toEither()

    }
}
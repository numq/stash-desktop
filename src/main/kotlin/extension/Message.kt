package extension

import file.DocumentFile
import file.File
import file.ImageFile
import org.apache.commons.codec.binary.Base64
import websocket.Message

val Message.isFile: Boolean
    get() = runCatching { body.has(File.NAME) && body.has(File.EXTENSION) && body.has(File.BYTES) }.isSuccess

val Message.file: File
    get() = with(body) {
        val extension = getString(File.EXTENSION)
        when {
            ImageFile.extensions.contains(extension) -> ImageFile(
                getString(File.NAME),
                extension,
                Base64.decodeBase64(getString(File.BYTES))
            )
            else -> DocumentFile(
                getString(File.NAME),
                extension,
                Base64.decodeBase64(getString(File.BYTES))
            )
        }
    }
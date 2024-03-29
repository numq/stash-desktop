package transfer

import file.FileRepository
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

class UploadFile constructor(
    private val repository: FileRepository
) : UseCase<Triple<String, String, ByteArray>, Unit>() {
    override suspend fun execute(arg: Triple<String, String, ByteArray>) =
        Right(arg).flatMap { (name, extension, bytes) -> repository.shareFile(name, extension, bytes) }
}
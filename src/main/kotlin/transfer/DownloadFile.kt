package transfer

import file.File
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

class DownloadFile constructor(
    private val service: TransferService
) : UseCase<Triple<String, String, File>, Unit>() {
    override suspend fun execute(arg: Triple<String, String, File>) =
        Right(arg).flatMap { (path, name, file) ->
            service.downloadFile(path, name, file.bytes)
        }
}
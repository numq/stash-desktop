package transfer

import action.CancellableAction
import extension.action
import file.File
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

class DownloadFile constructor(
    private val service: TransferService
) : UseCase<Triple<String, String, File>, CancellableAction>() {
    override suspend fun execute(arg: Triple<String, String, File>) =
        Right(arg).flatMap { (path, name, file) ->
            service.downloadFile(path, name, file.bytes).action()
        }
}
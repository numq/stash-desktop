package transfer

import action.CancellableAction
import extension.action
import file.File
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

class DownloadZip constructor(
    private val service: TransferService
) : UseCase<Triple<String, String, List<File>>, CancellableAction>() {
    override suspend fun execute(arg: Triple<String, String, List<File>>) =
        Right(arg).flatMap { (path, name, files) -> service.downloadZip(path, name, files).action() }
}
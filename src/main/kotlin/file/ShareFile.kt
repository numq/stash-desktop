package file

import action.CancellableAction
import extension.action
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

class ShareFile constructor(
    private val repository: FileRepository
) : UseCase<Triple<String, String, ByteArray>, CancellableAction>() {
    override suspend fun execute(arg: Triple<String, String, ByteArray>) =
        Right(arg).flatMap { (name, extension, bytes) -> repository.shareFile(name, extension, bytes).action() }
}
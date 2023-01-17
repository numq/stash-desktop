package file

import action.CancellableAction
import extension.action
import interactor.UseCase

class RemoveFile constructor(
    private val repository: FileRepository
) : UseCase<File, CancellableAction>() {
    override suspend fun execute(arg: File) = repository.removeFile(arg).action()
}
package folder

import action.CancellableAction
import extension.action
import interactor.UseCase

class StopSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, CancellableAction>() {
    override suspend fun execute(arg: Unit) = repository.stopSharing().action()
}
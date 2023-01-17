package folder

import action.CancellableAction
import extension.action
import interactor.UseCase

class StartSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, CancellableAction>() {
    override suspend fun execute(arg: Unit) = repository.startSharing().action()
}
package file

import action.CancellableAction
import extension.action
import interactor.UseCase

class RefreshFiles constructor(
    private val repository: FileRepository
) : UseCase<Unit, CancellableAction>() {
    override suspend fun execute(arg: Unit) = repository.refreshFiles().action()
}
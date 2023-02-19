package folder

import interactor.UseCase

class StartSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, Unit>() {
    override suspend fun execute(arg: Unit) = repository.startSharing()
}
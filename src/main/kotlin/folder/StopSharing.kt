package folder

import interactor.UseCase

class StopSharing constructor(
    private val repository: FolderRepository
) : UseCase<Unit, Unit>() {
    override suspend fun execute(arg: Unit) = repository.stopSharing()
}
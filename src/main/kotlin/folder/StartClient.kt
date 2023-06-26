package folder

import interactor.UseCase

class StartClient constructor(
    private val repository: FolderRepository,
) : UseCase<String?, Unit>() {
    override suspend fun execute(arg: String?) = repository.startClient(arg)
}
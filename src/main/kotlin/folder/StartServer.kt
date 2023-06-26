package folder

import interactor.UseCase

class StartServer constructor(
    private val repository: FolderRepository,
) : UseCase<Int?, Unit>() {
    override suspend fun execute(arg: Int?) = repository.startServer(arg)
}
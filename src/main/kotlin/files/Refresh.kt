package files

import interactor.UseCase

class Refresh constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override suspend fun execute(arg: Unit) = repository.refresh()
}
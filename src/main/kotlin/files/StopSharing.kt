package files

import interactor.UseCase

class StopSharing constructor(private val repository: FileRepository) : UseCase<Unit, Boolean>() {
    override fun execute(arg: Unit) = repository.stopSharing()
}
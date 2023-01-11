package file

import action.ActionStatus
import interactor.UseCase

class ClearFiles constructor(private val service: FileService) : UseCase<Unit, ActionStatus>() {
    override suspend fun execute(arg: Unit) = service.clear()
}
package file

import interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetEvents constructor(private val service: FileService) : UseCase<Unit, Flow<FileEvent>>() {
    override suspend fun execute(arg: Unit) = service.events
}
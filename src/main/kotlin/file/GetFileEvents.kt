package file

import interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetFileEvents constructor(
    private val repository: FileRepository
) : UseCase<Unit, Flow<FileEvent>>() {
    override suspend fun execute(arg: Unit) = repository.events
}
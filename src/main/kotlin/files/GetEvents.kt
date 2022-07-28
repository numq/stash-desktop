package files

import interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetEvents constructor(private val repository: FileRepository) : UseCase<Unit, Flow<FileEvent>>() {
    override fun execute(arg: Unit) = repository.events
}
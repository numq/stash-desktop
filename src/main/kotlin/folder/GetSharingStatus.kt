package folder

import interactor.UseCase
import kotlinx.coroutines.flow.Flow

class GetSharingStatus constructor(
    private val repository: FolderRepository
) : UseCase<Unit, Flow<SharingStatus>>() {
    override suspend fun execute(arg: Unit) = repository.sharingState
}
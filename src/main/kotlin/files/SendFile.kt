package files

import interactor.UseCase

class SendFile constructor(private val repository: FileRepository) : UseCase<ImageFile, Boolean>() {
    override suspend fun execute(arg: ImageFile) = repository.sendFile(arg)
}
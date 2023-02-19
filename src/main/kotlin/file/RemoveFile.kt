package file

import interactor.UseCase

class RemoveFile constructor(
    private val repository: FileRepository
) : UseCase<File, Unit>() {
    override suspend fun execute(arg: File) = repository.removeFile(arg)
}
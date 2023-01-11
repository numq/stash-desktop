package file

import action.ActionStatus
import interactor.UseCase
import it.czerwinski.kotlin.util.Either

class SendFile constructor(
    private val service: FileService
) : UseCase<Triple<String, String, ByteArray>, ActionStatus>() {
    override suspend fun execute(arg: Triple<String, String, ByteArray>): Either<Exception, ActionStatus> {
        val (name, extension, bytes) = arg
        return service.sendFile(name, extension, bytes)
    }
}
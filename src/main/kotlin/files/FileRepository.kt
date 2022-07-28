package files

import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    val events: Either<Exception, Flow<FileEvent>>
    fun clear(): Either<Exception, Boolean>
    fun startSharing(): Either<Exception, Boolean>
    fun stopSharing(): Either<Exception, Boolean>
    fun refresh(): Either<Exception, Boolean>
    fun sendFile(file: ImageFile): Either<Exception, Boolean>
}
package interactor

import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class UseCase<in T, out R> {

    abstract suspend fun execute(arg: T): Either<Exception, R>

    operator fun invoke(
        coroutineScope: CoroutineScope,
        arg: T,
        onException: (Exception) -> Unit,
        onResult: (R) -> Unit = {}
    ) {
        coroutineScope.launch {
            execute(arg).fold(onException, onResult)
        }
    }
}
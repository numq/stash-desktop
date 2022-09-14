package interactor

import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class UseCase<in T, out R> {

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    abstract suspend fun execute(arg: T): Either<Exception, R>

    operator fun invoke(arg: T, onException: (Exception) -> Unit, onResult: (R) -> Unit = {}) {
        coroutineScope.launch {
            execute(arg).fold(onException, onResult)
        }
    }
}
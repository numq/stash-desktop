package extension

import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

inline fun <reified R> catch(crossinline f: () -> R): Either<Exception, R> =
    runCatching(f).fold(::Right) {
        Left(Exception(it.message, it.cause))
    }

inline fun <reified R> catch(
    condition: Boolean,
    exception: Exception,
    crossinline f: () -> R
): Either<Exception, R> =
    if (condition) {
        runCatching(f).fold(::Right) {
            Left(Exception(it.message, it.cause))
        }
    } else Left(exception)

suspend inline fun <reified R> catchAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline f: suspend () -> R
): Either<Exception, R> =
    withContext(coroutineContext) {
        runCatching {
            f()
        }
    }.fold(::Right) {
        Left(Exception(it.message, it.cause))
    }

suspend inline fun <reified R> catchAsync(
    condition: Boolean,
    exception: Exception,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline f: suspend () -> R
): Either<Exception, R> =
    if (condition) {
        withContext(coroutineContext) {
            runCatching {
                f()
            }
        }.fold(::Right) {
            Left(Exception(it.message, it.cause))
        }
    } else Left(exception)
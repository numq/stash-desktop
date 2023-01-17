package extension

import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right

fun <T> Result<T>.toEither(): Either<Exception, T> =
    fold(onSuccess = ::Right, onFailure = ::Left).swap().map(::Exception).swap()

fun <T> Result<T>.toEither(condition: Boolean, exception: Exception): Either<Exception, T> =
    if (condition) fold(onSuccess = ::Right, onFailure = ::Left).swap().map(::Exception).swap() else Left(exception)
package extension

import action.CancellableAction
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap

fun <L, R> Either<L, R>.action(): Either<L, CancellableAction> =
    Right(CancellableAction.CANCELED).flatMap { this }.map { CancellableAction.COMPLETED }
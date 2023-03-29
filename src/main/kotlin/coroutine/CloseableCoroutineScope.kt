package coroutine

import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

internal class CloseableCoroutineScope(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext = dispatcher + SupervisorJob()
    override fun close() = coroutineContext.cancel()
}
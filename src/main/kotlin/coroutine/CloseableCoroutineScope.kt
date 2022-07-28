package coroutine

import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

internal class CloseableCoroutineScope
constructor(dispatcher: CoroutineDispatcher = Dispatchers.Main) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext = dispatcher + SupervisorJob()
    override fun close() = coroutineContext.cancel()
}
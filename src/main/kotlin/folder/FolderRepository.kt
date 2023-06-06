package folder

import extension.catch
import extension.catchAsync
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import websocket.ConnectionState
import websocket.SocketService

interface FolderRepository {

    val sharingState: Either<Exception, Flow<SharingStatus>>
    suspend fun startSharing(): Either<Exception, Boolean>
    suspend fun stopSharing(): Either<Exception, Unit>

    class Implementation constructor(
        private val client: SocketService.Client,
        private val server: SocketService.Server,
    ) : FolderRepository {

        override val sharingState = catch {
            client.connectionState.map {
                when (it) {
                    ConnectionState.DISCONNECTED -> SharingStatus.OFFLINE
                    ConnectionState.CONNECTING -> SharingStatus.CONNECTING
                    ConnectionState.CONNECTED -> SharingStatus.SHARING
                }
            }
        }

        override suspend fun startSharing() = catchAsync {
            client.start()
            server.start()
        }

        override suspend fun stopSharing() = catchAsync {
            client.stop()
            server.stop()
        }
    }
}
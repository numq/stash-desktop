package folder

import extension.toEither
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import websocket.ConnectionState
import websocket.SocketService

interface FolderRepository {

    val sharingState: Either<Exception, Flow<SharingStatus>>
    suspend fun startSharing(): Either<Exception, Unit>
    suspend fun stopSharing(): Either<Exception, Unit>

    class Implementation constructor(
        private val client: SocketService.Client,
        private val server: SocketService.Server
    ) : FolderRepository {

        override val sharingState = runCatching {
            client.connectionState.map {
                when (it) {
                    ConnectionState.DISCONNECTED -> SharingStatus.OFFLINE
                    ConnectionState.CONNECTING -> SharingStatus.CONNECTING
                    ConnectionState.CONNECTED -> SharingStatus.SHARING
                }
            }
        }.toEither()

        override suspend fun startSharing() = runCatching {
            server.start {
                client.start()
            }
        }.toEither()

        override suspend fun stopSharing() = runCatching {
            client.stop()
            server.stop()
        }.toEither()

    }
}
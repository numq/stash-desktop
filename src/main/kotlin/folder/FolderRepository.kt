package folder

import extension.catch
import extension.catchAsync
import it.czerwinski.kotlin.util.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import qr.QRGenerator
import websocket.ConnectionState
import websocket.SocketService

interface FolderRepository {

    val sharingState: Either<Exception, Flow<SharingStatus>>
    suspend fun startServer(port: Int?): Either<Exception, Unit>
    suspend fun startClient(address: String?): Either<Exception, Unit>
    suspend fun stopSharing(): Either<Exception, Unit>

    class Implementation constructor(
        private val client: SocketService.Client,
        private val server: SocketService.Server,
    ) : FolderRepository {

        override val sharingState = catch {
            client.connectionState.map { connection ->
                when (connection) {
                    is ConnectionState.Disconnected -> SharingStatus.Offline
                    is ConnectionState.Connecting -> SharingStatus.Connecting
                    is ConnectionState.Connected -> {
                        val address = connection.address.toString()
                        val qrCodePixels = runCatching { QRGenerator.generate(address) }.getOrNull()
                        SharingStatus.Sharing(
                            connection.isHost, qrCodePixels, address
                        )
                    }
                }
            }
        }

        override suspend fun startServer(port: Int?) = catchAsync {
            server.start(port)?.let { client.startWithAddress(it) } ?: throw Exception("Unable to launch server")
        }

        override suspend fun startClient(address: String?) = catchAsync {
            client.startWithString(address)
        }

        override suspend fun stopSharing() = catchAsync {
            client.stop()
            server.stop()
        }
    }
}
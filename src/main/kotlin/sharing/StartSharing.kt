package sharing

import action.ActionStatus
import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import it.czerwinski.kotlin.util.flatMap
import websocket.SocketService

class StartSharing constructor(
    private val client: SocketService.Client,
    private val server: SocketService.Server
) : UseCase<Unit, ActionStatus>() {
    override suspend fun execute(arg: Unit) =
        Right(ActionStatus.CANCELED).flatMap { server.start() }.flatMap { client.connect() }.map { ActionStatus.DONE }
}
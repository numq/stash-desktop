package transfer

import interactor.UseCase
import it.czerwinski.kotlin.util.Right
import kotlinx.coroutines.channels.Channel

class GetTransferActions constructor(
    private val service: TransferService
) : UseCase<Unit, Channel<TransferAction>>() {
    override suspend fun execute(arg: Unit) = service.actions
}
package transfer

import action.CancellableAction
import extension.action
import interactor.UseCase

class RequestTransfer constructor(
    private val service: TransferService
) : UseCase<TransferAction, CancellableAction>() {
    override suspend fun execute(arg: TransferAction) = service.requestTransfer(arg).action()
}
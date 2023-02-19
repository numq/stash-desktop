package transfer

import interactor.UseCase

class RequestTransfer constructor(
    private val service: TransferService
) : UseCase<TransferAction, Unit>() {
    override suspend fun execute(arg: TransferAction) = service.requestTransfer(arg)
}
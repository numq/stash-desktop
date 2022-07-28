package files

import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right
import sharing.SharingApi

class FileData constructor(
    private val sharingService: SharingApi
) : FileRepository {

    private fun <T> T.wrap(): Either<Exception, T> = runCatching { this }.fold({ Right(it) },
        { Left(Exception(it.localizedMessage)) })

    override val events = sharingService.events.wrap()

    override fun clear() = sharingService.clear().wrap()

    override fun startSharing() = sharingService.startSharing().wrap()

    override fun stopSharing() = sharingService.stopSharing().wrap()

    override fun refresh() = sharingService.refresh().wrap()

    override fun sendFile(file: ImageFile) = sharingService.shareFile(file).wrap()
}
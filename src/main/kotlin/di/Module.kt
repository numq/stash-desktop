package di

import config.Configuration
import file.*
import folder.*
import navigation.NavigationViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import transfer.*
import websocket.SocketClient
import websocket.SocketServer
import websocket.SocketService
import java.net.InetAddress

val socket = module {
    val hostname = InetAddress.getLocalHost().hostName
    single {
        SocketClient(
            String.format(
                SocketService.ADDRESS_PATTERN,
                hostname,
                Configuration.SOCKET_PORT
            )
        )
    } bind SocketService.Client::class
    single { SocketServer(hostname, Configuration.SOCKET_PORT) } bind SocketService.Server::class
}

val file = module {
    single { FileRepository.Implementation(get()) } bind FileRepository::class
    factory { GetFileEvents(get()) }
    factory { RefreshFiles(get()) }
    factory { ShareFile(get()) }
    factory { RemoveFile(get()) }
}

val folder = module {
    single { FolderRepository.Implementation(get(), get()) } bind FolderRepository::class
    factory { GetSharingStatus(get()) }
    factory { StartSharing(get()) }
    factory { StopSharing(get()) }
    single {
        FolderViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

val transfer = module {
    single { TransferService.Implementation() } bind TransferService::class
    factory { GetTransferActions(get()) } bind GetTransferActions::class
    factory { RequestTransfer(get()) } bind RequestTransfer::class
    factory { UploadFile(get()) } bind UploadFile::class
    factory { DownloadFile(get()) } bind DownloadFile::class
    factory { DownloadZip(get()) } bind DownloadZip::class
}

val navigation = module {
    single { NavigationViewModel(get(), get(), get(), get()) }
}

val appModule = socket + file + folder + transfer + navigation
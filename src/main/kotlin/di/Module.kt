package di

import config.Configuration
import file.*
import org.koin.dsl.bind
import org.koin.dsl.module
import sharing.SharingViewModel
import sharing.StartSharing
import sharing.StopSharing
import websocket.SocketClient
import websocket.SocketServer
import websocket.SocketService

val socket = module {
    single {
        SocketClient(
            String.format(
                SocketService.ADDRESS_PATTERN,
                Configuration.SOCKET_HOSTNAME,
                Configuration.SOCKET_PORT
            )
        )
    } bind SocketService.Client::class
    single { SocketServer(Configuration.SOCKET_HOSTNAME, Configuration.SOCKET_PORT) } bind SocketService.Server::class
}

val file = module {
    single { FileService.Implementation(get()) } bind FileService::class
    factory { ClearFiles(get()) }
    factory { StartSharing(get(), get()) }
    factory { StopSharing(get(), get()) }
    factory { GetEvents(get()) }
    factory { RefreshFiles(get()) }
    factory { SendFile(get()) }
}

val sharing = module {
    single {
        SharingViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}

val appModule = socket + file + sharing
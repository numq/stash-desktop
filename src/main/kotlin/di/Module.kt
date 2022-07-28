package di

import files.*
import org.koin.dsl.bind
import org.koin.dsl.module
import sharing.SharingApi
import sharing.SharingService
import websocket.WebSocketApi
import websocket.WebSocketClientService
import websocket.WebSocketServerService

val appModule = module {
    single { WebSocketClientService() } bind WebSocketApi.Client::class
    single { WebSocketServerService() } bind WebSocketApi.Server::class
    single { SharingService(get(), get()) } bind SharingApi::class
    single { FileData(get()) } bind FileRepository::class
    factory { ClearFiles(get()) }
    factory { StartSharing(get()) }
    factory { StopSharing(get()) }
    factory { GetEvents(get()) }
    factory { Refresh(get()) }
    factory { SendFile(get()) }
    single {
        FileViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}
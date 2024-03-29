package navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import error.ShowError
import folder.FolderScreen
import kotlinx.coroutines.delay
import notification.AnimatedNotification
import org.koin.java.KoinJavaComponent.inject
import transfer.DownloadDialog
import transfer.TransferAction
import transfer.UploadDialog

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation() {

    val scaffoldState = rememberScaffoldState()

    val vm: NavigationViewModel by inject(NavigationViewModel::class.java)

    vm.exception.collectAsState(null).value?.let { ShowError(scaffoldState, it) }

    val state by vm.state.collectAsState()

    state.action?.run {
        when (this) {
            is TransferAction.Upload -> UploadDialog(vm::uploadFiles, vm::completeAction)
            is TransferAction.DownloadFile -> DownloadDialog(
                targetName = file.name,
                targetExtension = file.extension,
                onDownload = { path, name ->
                    vm.downloadFile(path, name, file)
                },
                onCancel = vm::completeAction
            )

            is TransferAction.DownloadMultipleFiles -> files.forEach { file ->
                DownloadDialog(
                    targetName = file.name,
                    targetExtension = file.extension,
                    onDownload = { path, name ->
                        vm.downloadFile(path, name, file)
                    },
                    onCancel = vm::completeAction
                )
            }

            is TransferAction.DownloadZip -> DownloadDialog(
                targetName = "${System.currentTimeMillis()}",
                targetExtension = "zip",
                onDownload = { path, name ->
                    vm.downloadZip(path, name, files)
                },
                onCancel = vm::completeAction
            )
        }
    }

    LaunchedEffect(state.notifications) {
        state.notifications.forEach { notification ->
            delay(notification.millis)
            vm.showNotification(notification)
        }
    }

    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
            when (state.destination) {
                is Destination.Folder -> FolderScreen(vm::showNotification, vm.onException)
            }
            state.notifications.firstOrNull()?.let { notification ->
                AnimatedNotification(notification) {
                    vm.hideNotification(notification)
                }
            }
        }
    }
}
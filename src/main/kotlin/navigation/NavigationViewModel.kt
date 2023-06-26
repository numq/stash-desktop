package navigation

import file.File
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import notification.Notification
import transfer.DownloadFile
import transfer.DownloadZip
import transfer.GetTransferActions
import transfer.UploadFile
import viewmodel.StateViewModel

class NavigationViewModel constructor(
    private val getTransferActions: GetTransferActions,
    private val uploadFile: UploadFile,
    private val downloadFile: DownloadFile,
    private val downloadZip: DownloadZip,
) : StateViewModel<NavigationState>(NavigationState(Destination.Folder)) {

    private fun observeTransferEvents() {
        getTransferActions.invoke(viewModelScope, Unit, onException) { channel ->
            viewModelScope.launch {
                channel.consumeAsFlow().collect { action ->
                    updateState { it.copy(action = action) }
                }
            }
        }
    }

    init {
        observeTransferEvents()
    }

    fun uploadFiles(files: List<Triple<String, String, ByteArray>>) = viewModelScope.launch {
        files.forEach { (name, extension, bytes) ->
            uploadFile.invoke(viewModelScope, Triple(name, extension, bytes), onException)
        }
    }

    fun downloadFile(path: String, name: String, file: File) =
        downloadFile.invoke(viewModelScope, Triple(path, name, file), onException)

    fun downloadZip(path: String, name: String, files: List<File>) =
        downloadZip.invoke(viewModelScope, Triple(path, name, files), onException)

    fun completeAction() = updateState { it.copy(action = null) }

    fun showNotification(notification: Notification) = updateState {
        it.copy(notifications = it.notifications.plusElement(notification))
    }

    fun hideNotification(notification: Notification) = updateState {
        it.copy(notifications = it.notifications.minusElement(notification))
    }
}
package folder

import file.*
import kotlinx.coroutines.launch
import transfer.RequestTransfer
import transfer.TransferAction
import viewmodel.StateViewModel

class FolderViewModel constructor(
    private val getSharingStatus: GetSharingStatus,
    private val startClient: StartClient,
    private val startServer: StartServer,
    private val stopSharing: StopSharing,
    private val getFileEvents: GetFileEvents,
    private val refreshFiles: RefreshFiles,
    private val shareFile: ShareFile,
    private val removeFile: RemoveFile,
    private val requestTransfer: RequestTransfer,
) : StateViewModel<FolderState>(FolderState()) {

    private fun observeSharingStatus() = getSharingStatus.invoke(viewModelScope, Unit, onException) { sharingStatus ->
        viewModelScope.launch {
            sharingStatus.collect { sharingState ->
                if (sharingState is SharingStatus.Sharing) refreshFiles()
                updateState { it.copy(sharingStatus = sharingState) }
            }
        }
    }

    private fun observeFileEvents() = getFileEvents.invoke(viewModelScope, Unit, onException) { events ->
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is FileEvent.Refresh -> {
                        state.value.files.forEach { file ->
                            shareFile.invoke(
                                viewModelScope,
                                Triple(file.name, file.extension, file.bytes),
                                onException
                            )
                        }
                    }

                    is FileEvent.Upload -> {
                        updateState {
                            it.copy(files = listOf(event.file)
                                .filterNot { f -> it.files.any { file -> file.name == f.name } }
                                .plus(it.files)
                            )
                        }
                    }

                    is FileEvent.Delete -> {
                        updateState {
                            it.copy(files = it.files.filterNot { f -> f.name == event.file.name })
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    init {
        observeFileEvents()
        observeSharingStatus()
    }

    fun startServer(port: Int? = null) {
        updateState { it.copy(configurationVisible = false) }
        startServer.invoke(viewModelScope, port, onException) {
            updateState { it.copy(wasTheHost = true, lastUsedPort = port) }
        }
    }

    fun startClient(address: String? = null) {
        updateState { it.copy(configurationVisible = false) }
        startClient.invoke(viewModelScope, address, onException) {
            updateState { it.copy(wasTheHost = false, lastUsedAddress = address) }
        }
    }

    fun stopSharing() = stopSharing.invoke(viewModelScope, Unit, onException)

    fun refreshFiles() = refreshFiles.invoke(viewModelScope, Unit, onException)

    fun upload() = requestTransfer.invoke(viewModelScope, TransferAction.Upload, onException)

    fun downloadFile(file: File) =
        requestTransfer.invoke(viewModelScope, TransferAction.DownloadFile(file), onException)

    private fun downloadMultipleFiles(files: List<File>) =
        requestTransfer.invoke(viewModelScope, TransferAction.DownloadMultipleFiles(files), onException)

    private fun downloadZip(files: List<File>) =
        requestTransfer.invoke(viewModelScope, TransferAction.DownloadZip(files), onException)

    fun removeFile(file: File) {
        if (state.value.previewFile == file) nextFile(file)
        removeFile.invoke(viewModelScope, file, onException)
    }

    fun openFile(file: File) = updateState { it.copy(previewFile = file) }

    fun closeFile() = updateState { it.copy(previewFile = null) }

    fun previousFile(file: File) {
        updateState {
            val files =
                if (it.filteredByExtension) it.files.filter { f -> f.extension == file.extension }
                else it.files
            if (files.filterNot { f -> f.name == file.name }.isEmpty()) it.copy(previewFile = null)
            else {
                val prevIndex = files.indexOfFirst { f -> f.name == file.name }.minus(1)
                it.copy(previewFile = if (prevIndex < 0) files.last() else files.elementAt(prevIndex))
            }
        }
    }

    fun nextFile(file: File) {
        updateState {
            val files =
                if (it.filteredByExtension) it.files.filter { f -> f.extension == file.extension }
                else it.files
            if (files.filterNot { f -> f.name == file.name }.isEmpty()) it.copy(previewFile = null)
            else {
                val nextIndex = files.indexOfFirst { f -> f.name == file.name }.plus(1)
                it.copy(previewFile = if (nextIndex < files.size) files.elementAt(nextIndex) else files.first())
            }
        }
    }

    fun filterByExtension() = updateState { it.copy(filteredByExtension = !it.filteredByExtension) }

    fun enterSelection(file: File) {
        if (state.value.selectedFiles.isEmpty()) {
            updateState {
                it.copy(selectedFiles = listOf(file))
            }
        }
    }

    fun exitSelection() = updateState { it.copy(selectedFiles = emptyList()) }

    fun selectAll() = updateState { it.copy(selectedFiles = it.files) }

    fun manageSelection(file: File) {
        updateState {
            if (it.selectedFiles.contains(file)) it.copy(selectedFiles = it.selectedFiles.filterNot { f ->
                f.bytes.contentEquals(file.bytes)
            }) else it.copy(selectedFiles = it.selectedFiles.plus(file))
        }
        if (state.value.selectedFiles.isEmpty()) exitSelection()
    }

    fun downloadSelectedFiles() {
        viewModelScope.launch {
            downloadMultipleFiles(state.value.selectedFiles)
        }.invokeOnCompletion {
            exitSelection()
        }
    }

    fun downloadSelectedFilesAsZip() {
        viewModelScope.launch {
            downloadZip(state.value.selectedFiles)
        }.invokeOnCompletion {
            exitSelection()
        }
    }

    fun removeSelectedFiles() {
        viewModelScope.launch {
            state.value.selectedFiles.forEach(::removeFile)
        }.invokeOnCompletion {
            exitSelection()
        }
    }

    fun openNetworkInfo() {
        updateState { it.copy(networkInfoVisible = true) }
    }

    fun closeNetworkInfo() {
        updateState { it.copy(networkInfoVisible = false) }
    }

    fun openConfiguration() {
        updateState { it.copy(configurationVisible = true) }
    }

    fun cancelConfiguration() {
        updateState { it.copy(configurationVisible = false) }
    }
}
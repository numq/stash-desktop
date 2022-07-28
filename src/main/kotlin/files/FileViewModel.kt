package files

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import viewmodel.ViewModel

class FileViewModel constructor(
    getEvents: GetEvents,
    private val clearFiles: ClearFiles,
    private val startSharing: StartSharing,
    private val stopSharing: StopSharing,
    private val refresh: Refresh,
    private val sendFile: SendFile
) : ViewModel() {

    private val _state = MutableStateFlow(FilesState())
    val state: StateFlow<FilesState> = _state.asStateFlow()

    init {
        getEvents.invoke(Unit) {
            it.fold(onError) { events ->
                viewModelScope.launch {
                    events.collect { event ->
                        when (event) {
                            is FileEvent.Clear -> {
                                _state.update { s ->
                                    s.copy(imageFiles = emptyList())
                                }
                            }
                            is FileEvent.Refresh -> {
                                state.value.imageFiles.forEach { file ->
                                    sendFile.invoke(file) { result ->
                                        result.fold(onError) {}
                                    }
                                }
                            }
                            is FileEvent.File -> {
                                if (!state.value.imageFiles.contains(event.file)) {
                                    _state.update { s ->
                                        s.copy(imageFiles = s.imageFiles.plus(event.file))
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    fun clearFiles() = clearFiles.invoke(Unit) { it.fold(onError) {} }

    fun startSharing() =
        startSharing.invoke(Unit) {
            it.fold(onError) { connected ->
                if (connected) _state.update { s ->
                    s.copy(isSharing = connected)
                }.also { refresh() }
            }
        }

    fun stopSharing() =
        stopSharing.invoke(Unit) {
            it.fold(onError) {
                _state.update { s ->
                    s.copy(
                        isSharing = it,
                        imageFiles = emptyList()
                    )
                }
            }
        }

    fun refresh() = refresh.invoke(Unit) { it.fold(onError) {} }

    fun sendFile(file: ImageFile) = sendFile.invoke(file) { it.fold(onError) {} }
}
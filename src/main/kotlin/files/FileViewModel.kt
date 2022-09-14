package files

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import viewmodel.ViewModel

class FileViewModel constructor(
    private val getEvents: GetEvents,
    private val clearFiles: ClearFiles,
    private val startSharing: StartSharing,
    private val stopSharing: StopSharing,
    private val refresh: Refresh,
    private val sendFile: SendFile
) : ViewModel() {

    private val _state = MutableStateFlow(FilesState())
    val state: StateFlow<FilesState> = _state.asStateFlow()

    private val onError: (Exception) -> Unit = { e ->
        _state.update { it.copy(exception = e) }
    }

    private fun observeEvents() = getEvents.invoke(Unit, onError) { events ->
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
                            sendFile.invoke(file, onError)
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

    init {
        observeEvents()
    }

    fun clearFiles() = clearFiles.invoke(Unit, onError)

    fun startSharing() =
        startSharing.invoke(Unit, onError) { connected ->
            if (connected) _state.update { s ->
                s.copy(isSharing = connected)
            }.also { refresh() }
        }

    fun stopSharing() =
        stopSharing.invoke(Unit, onError) { connected ->
            _state.update { s ->
                s.copy(
                    isSharing = connected,
                    imageFiles = emptyList()
                )
            }
        }

    fun refresh() = refresh.invoke(Unit, onError)

    fun sendFile(file: ImageFile) = sendFile.invoke(file, onError)
}
package sharing

import action.ActionStatus
import file.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import viewmodel.StateViewModel

class SharingViewModel constructor(
    private val getEvents: GetEvents,
    private val clearFiles: ClearFiles,
    private val startSharing: StartSharing,
    private val stopSharing: StopSharing,
    private val refreshFiles: RefreshFiles,
    private val sendFile: SendFile
) : StateViewModel<SharingState>(SharingState()) {

    private fun observeEvents() = getEvents.invoke(viewModelScope, Unit, onException) { events ->
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is FileEvent.Clear -> {
                        updateState {
                            it.copy(files = emptyList())
                        }
                    }
                    is FileEvent.Refresh -> {
                        state.value.files.asFlow().onEach { file ->
                            sendFile.invoke(
                                viewModelScope,
                                Triple(file.name, file.extension, file.bytes),
                                onException
                            )
                        }.collect()
                    }
                    is FileEvent.File -> {
                        updateState {
                            if (it.files.contains(event.file).not()) {
                                return@updateState it.copy(files = listOf(event.file).plus(it.files))
                            }
                            it
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

    fun clearFiles() = clearFiles.invoke(viewModelScope, Unit, onException)

    fun startSharing() =
        startSharing.invoke(viewModelScope, Unit, onException) { status ->
            updateState {
                when (status) {
                    ActionStatus.CANCELED -> it.copy(isSharing = false)
                    ActionStatus.DONE -> it.copy(isSharing = true)
                }
            }
        }

    fun stopSharing() =
        stopSharing.invoke(viewModelScope, Unit, onException) { status ->
            updateState {
                when (status) {
                    ActionStatus.CANCELED -> it.copy(isSharing = true)
                    ActionStatus.DONE -> it.copy(isSharing = false)
                }
            }
        }

    fun refresh() = refreshFiles.invoke(viewModelScope, Unit, onException)

    fun sendFile(name: String, extension: String, bytes: ByteArray) =
        sendFile.invoke(viewModelScope, Triple(name, extension, bytes), onException)
}
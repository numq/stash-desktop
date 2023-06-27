package folder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import extension.countSuffix
import file.FileItem
import folder.container.InteractionContainer
import folder.container.SelectionContainer
import notification.Notification
import org.koin.java.KoinJavaComponent.inject

@Composable
fun FolderScreen(showNotification: (Notification) -> Unit, onException: (Exception) -> Unit) {

    val vm: FolderViewModel by inject(FolderViewModel::class.java)

    vm.exception.collectAsState(null).value?.let(onException)

    val state by vm.state.collectAsState()

    val isSharing = state.sharingStatus is SharingStatus.Sharing

    val selectionMode = state.selectedFiles.isNotEmpty()

    Scaffold(floatingActionButton = {
        AnimatedVisibility(selectionMode) {
            Row(
                Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card {
                    Row(
                        Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = vm::exitSelection) {
                            Icon(Icons.Rounded.Cancel, "exit selection")
                        }
                        IconButton(onClick = vm::selectAll) {
                            Icon(Icons.Rounded.DoneAll, "select all")
                        }
                    }
                }
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center, bottomBar = {
        BoxWithConstraints {
            val animatedOffset by animateDpAsState(if (state.previewFile == null) 0.dp else maxHeight)
            BottomAppBar(Modifier.offset(y = animatedOffset)) {
                Row(
                    Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val ActionBox: @Composable (@Composable () -> Unit) -> Unit = { content ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            content()
                        }
                    }
                    if (selectionMode) {
                        ActionBox {
                            IconButton(onClick = vm::removeSelectedFiles, enabled = isSharing) {
                                Icon(Icons.Rounded.DeleteForever, "remove selected files")
                            }
                        }
                        ActionBox {
                            IconButton(onClick = vm::downloadSelectedFiles) {
                                Icon(Icons.Rounded.Download, "download selected files")
                            }
                        }
                        ActionBox {
                            IconButton(onClick = vm::downloadSelectedFilesAsZip) {
                                Row(
                                    Modifier.padding(start = 4.dp, end = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("ZIP", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        val statusText = when (state.sharingStatus) {
                            is SharingStatus.Offline -> "Offline"
                            is SharingStatus.Connecting -> "Connecting"
                            is SharingStatus.Sharing -> "${state.files.count()} file${state.files.countSuffix} found"
                        }
                        ActionBox {
                            Text(text = statusText)
                        }
                        ActionBox {
                            IconButton(
                                onClick = vm::refreshFiles, enabled = isSharing
                            ) {
                                Icon(Icons.Rounded.Refresh, "refresh", modifier = Modifier.size(32.dp))
                            }
                        }
                        ActionBox {
                            IconButton(onClick = vm::upload, enabled = isSharing) {
                                Icon(Icons.Rounded.UploadFile, "upload file")
                            }
                        }
                        ActionBox {
                            when (state.sharingStatus) {
                                is SharingStatus.Sharing -> {
                                    val hostColor =
                                        if ((state.sharingStatus as SharingStatus.Sharing).isHost) Color.Green else Color.Yellow
                                    button.IconButton(
                                        onClick = if (state.networkInfoVisible) vm::closeNetworkInfo else vm::stopSharing,
                                        onLongClick = vm::openNetworkInfo
                                    ) {
                                        Icon(
                                            Icons.Rounded.CloudOff,
                                            "stop sharing",
                                            modifier = Modifier.size(32.dp),
                                            tint = hostColor
                                        )
                                    }
                                }

                                is SharingStatus.Connecting -> {
                                    val infiniteTransition = rememberInfiniteTransition()
                                    val angle by infiniteTransition.animateFloat(
                                        initialValue = 0F, targetValue = 360F, animationSpec = infiniteRepeatable(
                                            animation = tween(2000, easing = LinearEasing)
                                        )
                                    )
                                    IconButton(onClick = vm::stopSharing) {
                                        Icon(
                                            Icons.Rounded.Sync,
                                            "connecting",
                                            modifier = Modifier.size(32.dp).rotate(angle)
                                        )
                                    }
                                }

                                is SharingStatus.Offline -> {
                                    button.IconButton(onClick = {
                                        if (state.configurationVisible) vm.cancelConfiguration()
                                        else state.wasTheHost?.takeIf { it }?.let {
                                            vm.startServer(state.lastUsedPort)
                                        } ?: vm.startClient(state.lastUsedAddress)
                                    }, onLongClick = {
                                        if (!state.configurationVisible) vm.openConfiguration()
                                    }) {
                                        Icon(Icons.Rounded.Cloud, "start sharing", modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.BottomCenter
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.files, key = { it.name }) { file ->
                    if (selectionMode) {
                        SelectionContainer(
                            file = file,
                            isSelected = state.selectedFiles.any { it.name == file.name },
                            onClick = vm::manageSelection,
                            onLongClick = vm::manageSelection
                        ) {
                            FileItem(file)
                        }
                    } else {
                        InteractionContainer(
                            file = file,
                            isSharing = isSharing,
                            onClick = vm::openFile,
                            onLongClick = vm::enterSelection,
                            onRemove = vm::removeFile,
                            onSave = vm::downloadFile
                        ) {
                            FileItem(file)
                        }
                    }
                }
            }
            when (state.sharingStatus) {
                is SharingStatus.Offline -> {
                    AnimatedVisibility(state.configurationVisible,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }) {
                        ConfigurationInput(
                            state.wasTheHost,
                            state.lastUsedPort,
                            state.lastUsedAddress,
                            configureClient = { address ->
                                address?.let(vm::startClient) ?: vm.cancelConfiguration()
                            },
                            configureServer = { port ->
                                port?.let(vm::startServer) ?: vm.cancelConfiguration()
                            }
                        )
                    }
                }

                is SharingStatus.Sharing -> {
                    AnimatedVisibility(state.networkInfoVisible,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }) {
                        val status = state.sharingStatus as SharingStatus.Sharing
                        NetworkInfo(status.address, status.qrCodePixels, showNotification, vm::closeNetworkInfo)
                    }
                }

                else -> Unit
            }
        }
        state.previewFile?.let { file ->
            AnimatedVisibility(true) {
                PreviewFile(
                    file = file,
                    isSharing = state.sharingStatus is SharingStatus.Sharing,
                    controlsVisible = state.files.size > 1,
                    filteredByExtension = state.filteredByExtension,
                    filterByExtension = vm::filterByExtension,
                    previousFile = vm::previousFile,
                    nextFile = vm::nextFile,
                    downloadFile = vm::downloadFile,
                    removeFile = vm::removeFile,
                    closeFile = vm::closeFile
                )
            }
        }
    }
}
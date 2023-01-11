package sharing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import download.DownloadDialog
import download.DownloadType
import download.DownloadZipDialog
import error.ShowError
import file.DocumentFileItem
import file.ImageFile
import file.ImageFileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import upload.UploadDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharingScreen(
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {

    val vm: SharingViewModel by inject(SharingViewModel::class.java)

    vm.exception.consumeAsFlow().collectAsState(null).value?.let { exception ->
        ShowError(scaffoldState, exception)
    }

    val state by vm.state.collectAsState()
    if (state.isSharing) {
        vm.refresh()
    }

    val (uploadState, setUploadState) = remember { mutableStateOf(false) }
    if (uploadState) {
        UploadDialog(true, {
            vm.sendFile(it.nameWithoutExtension, it.extension, it.readBytes())
        }, {
            setUploadState(false)
        })
    }

    val (downloadType, setDownloadType) = remember { mutableStateOf<DownloadType?>(null) }
    val clearDownloadType: () -> Unit = {
        setDownloadType(null)
    }
    downloadType?.let {
        when (downloadType) {
            is DownloadType.Single -> DownloadDialog(
                downloadType.file,
                clearDownloadType
            )
            is DownloadType.Multiple -> downloadType.files.forEach { file ->
                DownloadDialog(file, clearDownloadType)
            }
            is DownloadType.Zip -> DownloadZipDialog(downloadType.files, clearDownloadType)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2), modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.files) { file ->
                when (file) {
                    is ImageFile -> ImageFileItem(file) {
                        setDownloadType(DownloadType.Single(it))
                    }
                    else -> DocumentFileItem(file) {
                        setDownloadType(DownloadType.Single(it))
                    }
                }
            }
        }
        if (state.files.isNotEmpty()) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                        elevation = null,
                        shape = RoundedCornerShape(0.dp),
                        onClick = {
                            setDownloadType(DownloadType.Multiple(state.files))
                        }) {
                        Text("DOWNLOAD ALL")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        elevation = null,
                        shape = RoundedCornerShape(0.dp),
                        onClick = {
                            setDownloadType(DownloadType.Zip(state.files))
                        }) {
                        Text("DOWNLOAD ZIP")
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                    elevation = null,
                    shape = RoundedCornerShape(0.dp),
                    onClick = {
                        coroutineScope.launch {
                            when (scaffoldState.snackbarHostState.showSnackbar(
                                "Do you want to clear all?",
                                "Yes, clear all",
                                SnackbarDuration.Short
                            )) {
                                SnackbarResult.ActionPerformed -> vm.clearFiles()
                                else -> Unit
                            }
                        }
                    }) {
                    Text("CLEAR ALL")
                }
            }
        }
        Divider()
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isSharing) {
                Text("Found ${state.files.count()} files", color = Color.Green)
                Button(onClick = { vm.refresh() }) {
                    Text("refresh")
                }
                Button(onClick = { setUploadState(true) }) {
                    Text("upload file")
                }
                Button(onClick = { vm.stopSharing() }) {
                    Text("stop sharing")
                }
            } else {
                Button(onClick = { vm.startSharing() }) {
                    Text("start sharing")
                }
            }
        }
    }
}
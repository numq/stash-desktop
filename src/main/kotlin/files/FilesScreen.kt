package files

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import download.DownloadDialog
import download.DownloadType
import download.DownloadZipDialog
import error.ShowError
import extension.base64
import extension.decodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skija.Image.makeFromEncoded
import org.koin.java.KoinJavaComponent.inject
import upload.UploadDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesScreen(
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {

    val vm: FileViewModel by inject(FileViewModel::class.java)

    val state by vm.state.collectAsState()

    val (currentIndex, setCurrentIndex) = remember {
        mutableStateOf(-1)
    }

    val (uploadState, setUploadState) = remember { mutableStateOf(false) }
    val (downloadType, setDownloadType) = remember { mutableStateOf<DownloadType?>(null) }

    fun upload() = setUploadState(true)

    if (uploadState) {
        UploadDialog(true, {
            vm.sendFile(ImageFile(it.extension, it.base64))
        }, {
            setUploadState(false)
        })
    }

    val clearDownloadType: () -> Unit = {
        setDownloadType(null)
    }

    downloadType?.let {
        when (downloadType) {
            is DownloadType.Single -> DownloadDialog(
                downloadType.file.extension,
                downloadType.file.blob.decodeBase64(),
                clearDownloadType
            )
            is DownloadType.Multiple -> downloadType.files.forEach { file ->
                DownloadDialog(file.extension, file.blob.decodeBase64(), clearDownloadType)
            }
            is DownloadType.Zip -> DownloadZipDialog(downloadType.files, clearDownloadType)
        }
    }

    state.exception?.let {
        ShowError(scaffoldState, it)
    }

    LaunchedEffect(state.imageFiles) {
        if (currentIndex < 0 && state.imageFiles.isNotEmpty()) setCurrentIndex(0)
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
            items(state.imageFiles) { file ->
                ImageFileItem(file) {
                    setDownloadType(DownloadType.Single(it))
                }
            }
        }
        if (state.imageFiles.isNotEmpty()) {
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
                            setDownloadType(DownloadType.Multiple(state.imageFiles))
                        }) {
                        Text("DOWNLOAD ALL")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        elevation = null,
                        shape = RoundedCornerShape(0.dp),
                        onClick = {
                            setDownloadType(DownloadType.Zip(state.imageFiles))
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
                Text("Found ${state.imageFiles.count()} files", color = Color.Green)
                Button(onClick = { vm.refresh() }) {
                    Text("refresh")
                }
                Button(onClick = { upload() }) {
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

@Composable
fun ImageFileItem(file: ImageFile, onClick: (ImageFile) -> Unit) {
    Image(
        bitmap = makeFromEncoded(file.blob.decodeBase64()).asImageBitmap(),
        contentDescription = "shared image",
        Modifier
            .fillMaxWidth()
            .clickable { onClick(file) }
    )
}
package transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog

@Composable
fun UploadDialog(onUpload: (List<Triple<String, String, ByteArray>>) -> Unit, onClose: () -> Unit) {

    val (visibility, setVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            ComposeWindow(),
            "Upload files",
            FileDialog.LOAD
        ).apply {
            isAlwaysOnTop = true
            isMultipleMode = true
            isVisible = visibility
        }
    ) {
        try {
            onUpload(files.map { Triple(it.nameWithoutExtension, it.extension, it.readBytes()) })
        } catch (e: Exception) {
            println("Upload dialog exception: ${e.localizedMessage}")
        } finally {
            setVisibility(false)
            onClose()
        }
    }
}
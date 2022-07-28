package upload

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.io.File

@Composable
fun UploadDialog(isMultiple: Boolean = false, onUpload: (File) -> Unit, onClose: () -> Unit) {

    val (dialogVisibility, setDialogVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            LocalAppWindow.current.window,
            "Upload file" + if (isMultiple) "s" else "",
            FileDialog.LOAD
        ).apply {
            isMultipleMode = isMultiple
            isVisible = dialogVisibility
        }) {
        try {
            files.forEach(onUpload)
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            setDialogVisibility(false)
            onClose()
        }
    }
}
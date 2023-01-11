package download

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import file.File
import java.awt.FileDialog
import java.io.FileOutputStream

@Composable
fun DownloadDialog(target: File, onClose: () -> Unit) {

    val (dialogVisibility, setDialogVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            LocalAppWindow.current.window,
            "Save file",
            FileDialog.SAVE
        ).apply {
            file = "${target.name}.${target.extension}"
            isVisible = dialogVisibility
        }) {
        try {
            FileOutputStream(directory + file).use {
                it.write(target.bytes)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            setDialogVisibility(false)
            onClose()
        }
    }
}

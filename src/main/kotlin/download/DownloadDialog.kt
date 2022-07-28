package download

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.io.FileOutputStream

@Composable
fun DownloadDialog(extension: String, data: ByteArray?, onClose: () -> Unit) {

    val (dialogVisibility, setDialogVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            LocalAppWindow.current.window,
            "Save file",
            FileDialog.SAVE
        ).apply {
            file = "${System.currentTimeMillis()}.$extension"
            isVisible = dialogVisibility
        }) {
        try {
            data?.let {
                FileOutputStream(directory + file).use {
                    it.write(data)
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            setDialogVisibility(false)
            onClose()
        }
    }
}

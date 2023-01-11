package download

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import file.File
import java.awt.FileDialog
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Composable
fun DownloadZipDialog(imageFiles: List<File>, onClose: () -> Unit) {

    val (dialogVisibility, setDialogVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            LocalAppWindow.current.window,
            "Save file",
            FileDialog.SAVE
        ).apply {
            file = "${System.currentTimeMillis()}" + ".zip"
            isVisible = dialogVisibility
        }) {
        try {
            ZipOutputStream(FileOutputStream(directory + file)).use { zip ->
                imageFiles.forEach { target ->
                    val name = "${target.name}.${target.extension}"
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(target.bytes)
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
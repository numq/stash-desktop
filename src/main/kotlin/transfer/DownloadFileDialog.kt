package transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog

@Composable
fun DownloadDialog(
    targetName: String,
    targetExtension: String,
    onDownload: (String, String) -> Unit,
    onCancel: () -> Unit,
) {

    val (visibility, setVisibility) = remember {
        mutableStateOf(true)
    }

    with(
        FileDialog(
            ComposeWindow(),
            "Save file",
            FileDialog.SAVE
        ).apply {
            file = "${targetName}.${targetExtension}"
            isAlwaysOnTop = true
            isVisible = visibility
        }) {
        try {
            onDownload(directory, if (file.endsWith(targetExtension)) file else file.plus(".$targetExtension"))
        } catch (e: Exception) {
            println("Download dialog exception: ${e.localizedMessage}")
        } finally {
            setVisibility(false)
            onCancel()
        }
    }
}

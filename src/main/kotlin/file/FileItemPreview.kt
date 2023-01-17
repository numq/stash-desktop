package file

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Image

@Composable
fun FilePreviewItem(file: File) {
    when (file) {
        is ImageFile -> {
            runCatching {
                Image.makeFromEncoded(file.bytes).toComposeImageBitmap()
            }.fold(onSuccess = {
                Image(
                    bitmap = it,
                    contentDescription = "image"
                )
            }, onFailure = {
                Icon(Icons.Rounded.Error, "failed to load image")
            })
        }
        else -> Text(file.extension, fontSize = 128.sp)
    }
}
package file

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.skija.Image

@Composable
fun ImageFileItem(file: ImageFile, onClick: (ImageFile) -> Unit) {
    Image(
        bitmap = Image.makeFromEncoded(file.bytes).asImageBitmap(),
        contentDescription = "shared image",
        Modifier
            .fillMaxWidth()
            .clickable { onClick(file) }
    )
}
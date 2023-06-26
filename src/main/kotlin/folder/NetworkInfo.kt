package folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import notification.Notification
import org.jetbrains.skia.Bitmap
import qr.QRGenerator

@Composable
fun NetworkInfo(
    address: String,
    qrCodePixels: ByteArray?,
    showNotification: (Notification) -> Unit,
    close: () -> Unit,
) {

    val clipboardManager = LocalClipboardManager.current

    DisposableEffect(Unit) {
        onDispose(close)
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            Modifier.background(MaterialTheme.colors.background).clickable(onClick = close).padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val qrCodeSize = QRGenerator.DEFAULT_SIZE
                qrCodePixels?.let { pixels ->
                    Image(
                        Bitmap().apply {
                            allocN32Pixels(qrCodeSize, qrCodeSize, true)
                            installPixels(pixels)
                        }.asComposeImageBitmap(), "qr code"
                    )
                } ?: run {
                    Icon(Icons.Rounded.BrokenImage, "broken QR code", Modifier.size(qrCodeSize.dp))
                    showNotification(Notification.Short("Unable to generate QR code"))
                }
            }
            Row(
                Modifier.clickable(onClick = {
                    clipboardManager.setText(AnnotatedString(address))
                    showNotification(Notification.Short("Copied to clipboard"))
                }).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(address)
                Icon(Icons.Rounded.ContentCopy, "copy address")
            }
        }
    }
}
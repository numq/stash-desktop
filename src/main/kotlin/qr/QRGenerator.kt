package qr

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.jetbrains.skia.Color

object QRGenerator {

    private val writer = QRCodeWriter()

    const val DEFAULT_SIZE = 100

    fun generate(text: String): ByteArray? = runCatching {
        writer.encode(text, BarcodeFormat.QR_CODE, DEFAULT_SIZE, DEFAULT_SIZE).run {
            val channels = 4
            val pixels = ByteArray(width * height * channels)
            repeat(width) { w ->
                repeat(height) { h ->
                    val offset = (h * width + w) * channels
                    val color = if (get(w, h)) Color.BLACK else Color.WHITE
                    repeat(channels) {
                        pixels[offset + it] = (if (it == 3) 255 else color).toByte()
                    }
                }
            }
            pixels
        }
    }.getOrNull()
}
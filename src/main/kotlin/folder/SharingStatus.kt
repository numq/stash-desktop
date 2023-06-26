package folder

sealed class SharingStatus private constructor() {
    object Offline : SharingStatus()
    object Connecting : SharingStatus()
    data class Sharing(val isHost: Boolean, val qrCodePixels: ByteArray?, val address: String) : SharingStatus() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Sharing

            if (isHost != other.isHost) return false
            if (qrCodePixels != null) {
                if (other.qrCodePixels == null) return false
                if (!qrCodePixels.contentEquals(other.qrCodePixels)) return false
            } else if (other.qrCodePixels != null) return false
            return address == other.address
        }

        override fun hashCode(): Int {
            var result = isHost.hashCode()
            result = 31 * result + (qrCodePixels?.contentHashCode() ?: 0)
            result = 31 * result + address.hashCode()
            return result
        }
    }
}
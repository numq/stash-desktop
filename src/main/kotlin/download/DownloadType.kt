package download

import files.ImageFile

sealed class DownloadType private constructor() {
    data class Single(val file: ImageFile) : DownloadType()
    data class Multiple(val files: List<ImageFile>) : DownloadType()
    data class Zip(val files: List<ImageFile>) : DownloadType()
}
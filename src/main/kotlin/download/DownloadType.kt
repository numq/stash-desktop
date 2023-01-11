package download

import file.File

sealed class DownloadType private constructor() {
    data class Single(val file: File) : DownloadType()
    data class Multiple(val files: List<File>) : DownloadType()
    data class Zip(val files: List<File>) : DownloadType()
}
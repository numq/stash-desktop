package file

sealed class FileEvent private constructor() {
    object Empty : FileEvent()
    object Clear : FileEvent()
    object Refresh : FileEvent()
    data class File(val file: file.File) : FileEvent()
}
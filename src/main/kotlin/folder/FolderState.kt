package folder

import file.File

data class FolderState(
    val sharingStatus: SharingStatus = SharingStatus.Offline,
    val files: List<File> = emptyList(),
    val previewFile: File? = null,
    val filteredByExtension: Boolean = false,
    val selectedFiles: List<File> = emptyList(),
    val copiedTextToClipboard: Boolean = false,
    val networkInfoVisible: Boolean = false,
    val configurationVisible: Boolean = false,
)
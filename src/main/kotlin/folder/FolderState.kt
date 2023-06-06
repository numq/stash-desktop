package folder

import file.File

data class FolderState(
    val isHost: Boolean = false,
    val sharingStatus: SharingStatus = SharingStatus.OFFLINE,
    val files: List<File> = emptyList(),
    val previewFile: File? = null,
    val filteredByExtension: Boolean = false,
    val selectedFiles: List<File> = emptyList(),
)
package folder

import file.File

data class FolderState(
    val sharingStatus: SharingStatus = SharingStatus.Offline,
    val wasTheHost: Boolean? = null,
    val lastUsedPort: Int? = null,
    val lastUsedAddress: String? = null,
    val files: List<File> = emptyList(),
    val previewFile: File? = null,
    val filteredByExtension: Boolean = false,
    val selectedFiles: List<File> = emptyList(),
    val networkInfoVisible: Boolean = false,
    val configurationVisible: Boolean = false,
)
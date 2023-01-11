package sharing

import file.File

data class SharingState(
    val isSharing: Boolean = false,
    val files: List<File> = emptyList()
)
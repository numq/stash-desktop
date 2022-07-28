package sharing

import files.FileEvent
import files.ImageFile
import kotlinx.coroutines.flow.Flow

interface SharingApi {
    val events: Flow<FileEvent>
    fun clear(): Boolean
    fun refresh(): Boolean
    fun startSharing(): Boolean
    fun stopSharing(): Boolean
    fun shareFile(file: ImageFile): Boolean
}
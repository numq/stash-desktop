package navigation

import notification.Notification
import transfer.TransferAction

data class NavigationState(
    val destination: Destination,
    val action: TransferAction? = null,
    val notifications: Set<Notification> = setOf(),
)
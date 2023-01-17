package navigation

import transfer.TransferAction

data class NavigationState(
    val destination: Destination,
    val action: TransferAction? = null
)
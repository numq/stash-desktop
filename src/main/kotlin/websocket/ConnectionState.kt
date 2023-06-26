package websocket

sealed class ConnectionState private constructor() {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(val isHost: Boolean, val address: SocketAddress) : ConnectionState()
}
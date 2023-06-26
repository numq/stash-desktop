package websocket

data class SocketAddress(
    val protocol: String = "ws",
    val hostname: String = SocketService.DEFAULT_HOSTNAME,
    val port: Int = SocketService.DEFAULT_PORT,
) {
    override fun toString() = "%s://%s:%s".format(protocol, hostname, port)
}
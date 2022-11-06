package cat.kiwi.minecraft.abcregister.model

import java.net.InetSocketAddress

data class ServerStatus(
    val uuid: String,
    val gameType: String,
    val displayName: String,
    val currentOnline: Int,
    val players: Collection<String>,
    val total: Int,
    val gameStatus: GameStatus,
    val address: String,
    val port: Int,
    val version: String,
    val meta: String
)

val ServerStatus.serverName: String
    get() = address.replace(".", "-") + "-$port"

val ServerStatus.inetAddress: InetSocketAddress
    get() = InetSocketAddress(address, port)
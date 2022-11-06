package cat.kiwi.minecraft.abcregister.service

import cat.kiwi.minecraft.abcregister.config.Config
import cat.kiwi.minecraft.abcregister.model.ServerStatus
import cat.kiwi.minecraft.abcregister.model.inetAddress
import cat.kiwi.minecraft.abcregister.model.serverName
import cat.kiwi.minecraft.abcregister.utils.Logger
import com.google.gson.Gson
import io.etcd.jetcd.watch.WatchResponse
import  io.etcd.jetcd.watch.WatchEvent.EventType.PUT
import net.md_5.bungee.api.ProxyServer
import java.net.InetSocketAddress

object WatchTask {
    fun appendHandler(watchResponse: WatchResponse) {
        val gson = Gson()
        val events = watchResponse.events
        events.forEach {
            val type = it.eventType
            val kv = it.keyValue
            Logger.debug("type: $type, key: ${kv.key.toString(Charsets.UTF_8)}, value: ${kv.value.toString(Charsets.UTF_8)}")
            if (type == PUT) {
                val serverStatus = gson.fromJson(kv.value.toString(Charsets.UTF_8), ServerStatus::class.java)
                val serverName = serverStatus.serverName
                val isAddress = serverStatus.inetAddress
                val motd = serverStatus.uuid
                val serverInfo = ProxyServer.getInstance().constructServerInfo(serverName, isAddress, motd, false)
                ProxyServer.getInstance().servers[serverName] = serverInfo
                Logger.debug("serverInfo: ${ProxyServer.getInstance().servers}")
            } else {
                if (Config.deleteAfterGrant) {
                    val serverStatus = gson.fromJson(kv.value.toString(Charsets.UTF_8), ServerStatus::class.java)
                    val serverName = serverStatus.address.replace(".", "-") + "-${serverStatus.port}"
                    ProxyServer.getInstance().servers.remove(serverName)
                }
            }
        }
    }
}
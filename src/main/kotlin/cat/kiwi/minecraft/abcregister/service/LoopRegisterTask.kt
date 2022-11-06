package cat.kiwi.minecraft.abcregister.service

import cat.kiwi.minecraft.abcregister.ABCRegisterPlugin
import cat.kiwi.minecraft.abcregister.config.Config
import cat.kiwi.minecraft.abcregister.model.ServerStatus
import cat.kiwi.minecraft.abcregister.model.inetAddress
import cat.kiwi.minecraft.abcregister.model.serverName
import cat.kiwi.minecraft.abcregister.utils.Logger
import com.google.gson.Gson
import io.etcd.jetcd.ByteSequence
import io.etcd.jetcd.options.GetOption
import io.etcd.jetcd.watch.WatchEvent
import io.etcd.jetcd.watch.WatchResponse
import net.md_5.bungee.api.ProxyServer
import java.net.InetSocketAddress

object LoopRegisterTask {
    fun loopHandler() {
        if (!Config.loopRegister) return
        val gson = Gson()
        // get jetcdkv client
        val kvClient = ABCRegisterPlugin.client.kvClient
        // get server status
        kvClient.get(
            ByteSequence.from("/agenda/service/", Charsets.UTF_8),
            GetOption.newBuilder().isPrefix(true).build()
        ).get().kvs.forEach {
            val serverStatus = gson.fromJson(it.value.toString(Charsets.UTF_8), ServerStatus::class.java)
            Logger.debug("serverStatus: $serverStatus")
            // register server
            val serverName = serverStatus.serverName
            val isAddress = serverStatus.inetAddress
            val motd = serverStatus.uuid
            val serverInfo = ProxyServer.getInstance().constructServerInfo(serverName, isAddress, motd, false)
            ProxyServer.getInstance().servers[serverName] = serverInfo
            Logger.debug("serverInfo: ${ProxyServer.getInstance().servers}")
        }
    }
}
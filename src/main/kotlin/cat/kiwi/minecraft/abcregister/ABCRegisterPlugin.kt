package cat.kiwi.minecraft.abcregister

import cat.kiwi.minecraft.abcregister.config.Config
import cat.kiwi.minecraft.abcregister.service.WatchTask
import com.google.gson.Gson
import io.etcd.jetcd.ByteSequence
import io.etcd.jetcd.Client
import io.etcd.jetcd.options.WatchOption
import net.md_5.bungee.api.plugin.Plugin

class ABCRegisterPlugin : Plugin() {
    companion object {
        lateinit var instance: ABCRegisterPlugin
    }

    override fun onEnable() {
        instance = this

        Config.makeConfig()
        Config.readConfig()

        // create jetcd client
        val client = Client.builder().endpoints(Config.etcdEndpoints).build()

        logger.info("ABCRegisterPlugin enabled")
        // run task async
        proxy.scheduler.runAsync(this) {
            // watch client
            client.watchClient.watch(
                ByteSequence.from("/agenda/service/", Charsets.UTF_8),
                WatchOption.newBuilder().isPrefix(true).build(),
                WatchTask::appendHandler
            )
        }
    }
}
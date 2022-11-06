package cat.kiwi.minecraft.abcregister

import cat.kiwi.minecraft.abcregister.config.Config
import cat.kiwi.minecraft.abcregister.model.ServerStatus
import cat.kiwi.minecraft.abcregister.service.LoopRegisterTask
import cat.kiwi.minecraft.abcregister.service.WatchTask
import com.google.gson.Gson
import io.etcd.jetcd.ByteSequence
import io.etcd.jetcd.Client
import io.etcd.jetcd.options.WatchOption
import net.md_5.bungee.api.plugin.Plugin

class ABCRegisterPlugin : Plugin() {
    companion object {
        lateinit var instance: ABCRegisterPlugin
        lateinit var client: Client
    }

    override fun onEnable() {
        instance = this

        Config.makeConfig()
        Config.readConfig()

        // create jetcd client
        client = Client.builder().endpoints(Config.etcdEndpoints).build()

        logger.info("ABCRegisterPlugin enabled")
        // run watcher async
        proxy.scheduler.runAsync(this) {
            // watch client
            client.watchClient.watch(
                ByteSequence.from("/agenda/service/", Charsets.UTF_8),
                WatchOption.newBuilder().isPrefix(true).build(),
                WatchTask::appendHandler
            )
        }

        // run loop register async
        proxy.scheduler.runAsync(this) {
            while (Config.loopRegister) {
                try {
                    LoopRegisterTask.loopHandler()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Thread.sleep(Config.loopRegisterInterval)
            }
        }
    }
}
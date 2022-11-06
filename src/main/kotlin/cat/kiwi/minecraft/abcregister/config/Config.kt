package cat.kiwi.minecraft.abcregister.config

import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI


object Config {
    lateinit var etcdEndpoints: ArrayList<URI>
    var debug = false
    var deleteAfterGrant = false
    var loopRegister = true
    var loopRegisterInterval = 60000L

    @Throws(IOException::class)
    fun makeConfig() {
        val instance = cat.kiwi.minecraft.abcregister.ABCRegisterPlugin.instance
        // Create plugin config folder if it doesn't exist
        if (!instance.dataFolder.exists()) {
            instance.logger.info("Created config folder: " + instance.dataFolder.mkdir())
        }
        val configFile = File(instance.dataFolder, "config.yml")

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            val outputStream = FileOutputStream(configFile) // Throws IOException
            val `in`: InputStream =
                instance.getResourceAsStream("config.yml") // This file must exist in the jar resources folder
            `in`.transferTo(outputStream) // Throws IOException
        }
    }

    fun readConfig() {
        val instance = cat.kiwi.minecraft.abcregister.ABCRegisterPlugin.instance
        val logger = instance.logger


        val unFindConfig = mutableListOf<String>()
        try {
            val configuration: Configuration = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(
                File(instance.dataFolder, "config.yml")
            )
            etcdEndpoints = configuration.getStringList("etcd.endpoints").let { it ->
                val list = arrayListOf<URI>()
                it.forEach {
                    list.add(URI.create(it))
                }
                list
            }
            debug = configuration.getBoolean("debug")
            deleteAfterGrant = configuration.getBoolean("deleteAfterGrant")
            loopRegister = configuration.getBoolean("loopRegister")
            loopRegisterInterval = configuration.getLong("loopRegisterInterval")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.warning("[UltimateInventoryShop]Config file is not correct!")
            logger.warning("[UltimateInventoryShop]配置文件读取失败")
            instance.onDisable()
        }
        if (unFindConfig.isNotEmpty()) {
            instance.logger.warning("未找到以下配置项(Unable to find these config): ${unFindConfig.joinToString(", ")}")
        }
    }
}
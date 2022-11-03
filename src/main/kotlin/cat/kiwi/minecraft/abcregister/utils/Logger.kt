package cat.kiwi.minecraft.abcregister.utils

import cat.kiwi.minecraft.abcregister.ABCRegisterPlugin
import cat.kiwi.minecraft.abcregister.config.Config

object Logger {
    fun debug(any: Any?) {
        if (Config.debug) {
            ABCRegisterPlugin.instance.logger.info("[DEBUG] $any")
        }
    }
    fun debug(any: Any?, javaClass: Class<*>) {
        if (Config.debug) {
            ABCRegisterPlugin.instance.logger.info("[DEBUG] ${javaClass.simpleName}: $any")
        }
    }
}
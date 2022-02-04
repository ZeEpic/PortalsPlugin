package me.zeepic.portals

import me.zeepic.portals.listener.BlockInteractListener
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(BlockInteractListener, this)

    }

}
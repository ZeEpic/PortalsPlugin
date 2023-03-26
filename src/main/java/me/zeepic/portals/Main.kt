package me.zeepic.portals

import me.zeepic.portals.listener.BlockInteractListener
import me.zeepic.portals.listener.FireListener
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {

        // Register listeners
        server.pluginManager.registerEvents(BlockInteractListener, this)
        server.pluginManager.registerEvents(FireListener, this)

    }

}
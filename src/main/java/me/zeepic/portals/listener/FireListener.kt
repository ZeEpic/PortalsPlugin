package me.zeepic.portals.listener

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockSpreadEvent

object FireListener : Listener {

    @EventHandler
    fun onFireSpread(event: BlockSpreadEvent) {

        val block = event.block

        if (block.type != Material.FIRE) return

        // We are only focused on the end dimension
        if (block.world.environment != World.Environment.THE_END) return

        // Make the portal
        createPortalAt(block)

    }

}
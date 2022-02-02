package me.zeepic.portals.listener

import me.zeepic.portals.attemptFindNetherPortal
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Orientable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class BlockInteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

        // Find the item in hand
        val hand = event.item ?: return

        // Find the block the player intended to click on
        val block = event.clickedBlock
            ?.getRelative(event.blockFace)
            ?: return

        // When the player attempts to light a portal
        if (hand.type != Material.FLINT_AND_STEEL) return

        // We are only focused on the end dimension
        if (block.world.environment != World.Environment.THE_END) return

        // Actually fill in the portal
        attemptCreatePortal(block)

    }

}

private fun attemptCreatePortal(block: Block) {

    // Use the utility function to find the bounds
    val portal = block.attemptFindNetherPortal() ?: return

    // Fill the bounds and orient the portal on the correct axis
    portal.attemptFill(Material.NETHER_PORTAL) {
        val data = it as Orientable
        data.axis = portal.axis
        data
    }

}
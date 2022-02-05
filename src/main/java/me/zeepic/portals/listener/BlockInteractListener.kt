package me.zeepic.portals.listener

import me.zeepic.portals.attemptFindNetherPortal
import me.zeepic.portals.playSound
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.Damageable

object BlockInteractListener : Listener {

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
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        // We are only focused on the end dimension
        if (block.world.environment != World.Environment.THE_END) return

        // Use the utility function to find the bounds of the portal
        val portal = block.attemptFindNetherPortal() ?: return

        // Actually fill in the portal
        portal.attemptFill()

        // Effects
        block.playSound(Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS)

        // Remove durability
        val copy = hand.clone()
        val meta = copy.itemMeta as Damageable
        meta.damage += 1 // Adding damage causes the durability to go down
        copy.itemMeta = meta

        // Set item in player's hand to the copy
        val inv = event.player.inventory
        if (hand == inv.itemInMainHand) inv.setItemInMainHand(copy)
        if (hand == inv.itemInOffHand) inv.setItemInOffHand(copy)

    }

}

package me.zeepic.portals.listener

import me.zeepic.portals.attemptFindNetherPortal
import me.zeepic.portals.playSound
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object BlockInteractListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

        val player = event.player

        // Verify player's gamemode
        if (player.gameMode == GameMode.ADVENTURE) return

        // Find the item in hand
        val hand = event.item ?: return

        // Find the block the player intended to click on
        val block = event.clickedBlock
            ?.getRelative(event.blockFace)
            ?: return

        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        // We are only focused on the end dimension
        if (block.world.environment != World.Environment.THE_END) return

        // Verify clicked block
        if (block.type != Material.FIRE && block.type != Material.AIR) return

        when (hand.type) {
            Material.FLINT_AND_STEEL -> {
                val result = createPortalAt(block)
                if (!result) return
                event.isCancelled = true
                handleUseOfFlintAndSteel(block, player, event.hand!!)
            }
            Material.FIRE_CHARGE -> {
                val result = createPortalAt(block)
                if (!result) return
                event.isCancelled = true
                handleUseOfFireCharge(block, hand, player, event.hand!!)
            }
            else -> return
        }

    }

}

fun createPortalAt(block: Block): Boolean {

    // Use the utility function to find the bounds of the portal
    val portal = block.attemptFindNetherPortal() ?: return false

    // Actually fill in the portal
    val result = portal.attemptFill()
    if (!result) return false
    return true
}

private fun handleUseOfFlintAndSteel(
    block: Block,
    player: Player,
    slot: EquipmentSlot
) {
    block.playSound(Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS)

    if (player.gameMode != GameMode.CREATIVE) {
        player.damageItemStack(slot, 1)
    }

    player.swingHand(slot)
}

private fun handleUseOfFireCharge(
    block: Block,
    hand: ItemStack,
    player: Player,
    slot: EquipmentSlot
) {
    // Effects
    block.playSound(Sound.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS)

    // Remove fire charge
    val copy = hand.clone()
    if (player.gameMode != GameMode.CREATIVE) {
        copy.amount -= 1
        if (copy.amount <= 0) copy.type = Material.AIR
    }
    player.inventory.setItem(slot, copy)
    player.swingHand(slot)
}

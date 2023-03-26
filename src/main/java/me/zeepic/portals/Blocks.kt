package me.zeepic.portals

import org.bukkit.Axis
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Orientable
import kotlin.math.abs


/**
 * Locates the furthest block in the direction of face.
 * Example: this.type = air and face = DOWN.
 * The function would find the block at the floor below this one.
 *
 * @param face The direction to find the end.
 * @param checkAdjacent To be sure there is an adjacent material at all times
 * @param adjacentDirection The direction to check for the adjacent material
 *
 * @return The block at the end, null if the adjacent check is not met, or null if there is no end found within 21 blocks.
 *
 */
fun Block.getEnd(face: BlockFace, checkAdjacent: Material? = null, adjacentDirection: BlockFace? = null): Block? {
    var end = this
    for (i in 0..21) {
        val relative = end.getRelative(face)
        if (checkAdjacent != null && adjacentDirection != null ) {
            if (end.getRelative(adjacentDirection).type != checkAdjacent) return null
        }
        if (relative.type != Material.AIR) return end // We found the end!
        end = relative
    }
    return null
}

/**
 * A class which marks the bounds of a nether portal. It represents the inside of the portal (i.e. not the obsidian).
 *
 */
data class NetherPortal(
    val corner1: Block,
    val corner2: Block,
    val axis: Axis
) {

    /**
     * Simply fills the portal.
     *
     * @return True if the nether portal was filled successfully.
     *
     */
    fun attemptFill(): Boolean {
        if ((abs(corner1.x - corner2.x) < 1 && abs(corner1.z - corner2.z) < 1) || abs(corner1.y - corner2.y) < 2) return false
        val result = corner1.loopBlocksWhile(corner2) {
            it.type.isEmpty || it.type == Material.FIRE
        }
        if (!result) return false
        corner1.loopBlocksWhile(corner2) {
            it.type = Material.NETHER_PORTAL
            val data = it.blockData as Orientable
            data.axis = axis
            it.blockData = data
            true
        }
        return true
    }

}

val portalMaterial = Material.OBSIDIAN
val cardinals = mapOf(
    BlockFace.NORTH to Axis.Z,
    BlockFace.SOUTH to Axis.Z,
    BlockFace.EAST to Axis.X,
    BlockFace.WEST to Axis.X
)

/**
 * Given a block inside the nether portal, this function will determine the direction that the portal faces and find it's bounds.
 *
 * @return The bounds if the portal is built correctly, or null if there was any sort of unexpected shape in the portal frame or inside.
 *
 */
fun Block.attemptFindNetherPortal(): NetherPortal? {

    val bottom = getEnd(BlockFace.DOWN) ?: return null
    val floor = bottom.getRelative(BlockFace.DOWN)
    if (floor.type != portalMaterial) return null
    return cardinals
        .filter { (face, _) -> floor.getRelative(face).type == portalMaterial }
        .mapNotNull { (face, axis) -> bottom.netherPortalBounds(face, axis) }
        .firstOrNull()

}

/**
 * Calculates the bounds of a nether portal if the axis is known. This block must be on the floor of the portal.
 *
 * @param face The direction the portal is facing.
 * @param axis The axis to pass onto the bounds.
 *
 * @return The bounds of the portal if found, or null if there was a hole in the portal frame.
 *
 */
fun Block.netherPortalBounds(face: BlockFace, axis: Axis): NetherPortal? {

    val opposite = face.oppositeFace

    // Get required portal edges
    val bottomLeft = getEnd(face, portalMaterial, BlockFace.DOWN) ?: return null
    val topLeft = bottomLeft.getEnd(BlockFace.UP, portalMaterial, face) ?: return null
    val topRight = topLeft.getEnd(opposite, portalMaterial, BlockFace.UP) ?: return null

    // Verify portal is completed
    getEnd(opposite, portalMaterial, BlockFace.DOWN) ?: return null
    topRight.getEnd(BlockFace.DOWN, portalMaterial, opposite) ?: return null

    return NetherPortal(bottomLeft, topRight, axis)

}

/**
 * This function simply runs the given function of each block in the range. It will exit if the function returns false.
 *
 * @param block The opposite block to loop towards.
 * @param function The function to run on each block in the loop. Return false to exit.
 *
 * @return False if there were any blocks whose function returned false.
 *
 */
fun Block.loopBlocksWhile(block: Block, function: (Block) -> Boolean): Boolean {
    for (i in x loopTo block.x) {
        for (j in y loopTo block.y) {
            for (k in z loopTo block.z) {
                if (!function(world.getBlockAt(i, j, k))) {
                    return false
                }
            }
        }
    }
    return true
}

/**
 * Used to loop between two unknown integers.
 */
private infix fun Int.loopTo(other: Int): IntRange {

    return if (other >= this) this..other
    else other..this

}

/**
 * Utility function to play sounds at blocks
 *
 * @param sound The sound to play
 * @param category The sound category to use
 *
 */
fun Block.playSound(sound: Sound, category: SoundCategory = SoundCategory.MASTER)
    = world.playSound(location, sound, category, 1f, 1f)
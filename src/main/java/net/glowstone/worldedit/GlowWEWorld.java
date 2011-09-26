package net.glowstone.worldedit;

import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import net.glowstone.GlowWorld;

public class GlowWEWorld extends BukkitWorld {
    
    /**
     * Construct the object.
     *
     * @param world
     */
    public GlowWEWorld(GlowWorld world) {
        super(world);
    }

    @Override
    public boolean isValidBlockType(int id) {
        return BlockType.fromID(id) != null;
    }
}

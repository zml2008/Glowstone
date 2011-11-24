package net.glowstone.block.physics;

import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.data.Attachable;
import net.glowstone.block.data.ToggleableAttachable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;

public class ToggleableAttachablePhysics extends AttachablePhysics {
    private final ToggleableAttachable data;

    public ToggleableAttachablePhysics(ToggleableAttachable data) {
        super(data);
        this.data = data;
    }


    @Override
    public int getPlacedMetadata(int current, BlockFace against) {
        return data.setAttachedFace(current, against);
    }

    public boolean interact(GlowPlayer player, GlowBlock block, boolean rightClick, BlockFace against) {
        block.setData((byte)data.toggleOpen(block.getData()));
        return false;
    }
}

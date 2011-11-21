package net.glowstone.block.physics;


import net.glowstone.block.data.Attachable;
import org.bukkit.block.BlockFace;

public class AttachablePhysics extends DefaultBlockPhysics {
    private Attachable data;

    public AttachablePhysics(Attachable data) {
        this.data = data;
    }
    
    @Override
    public int getPlacedMetadata(int current, BlockFace against) {
        return data.setAttachedFace(current, against);
    }
}

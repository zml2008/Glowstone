package net.glowstone.block.physics;


import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.data.Attachable;
import org.bukkit.block.BlockFace;

public class AttachablePhysics extends DefaultBlockPhysics {
    protected final Attachable data;

    public AttachablePhysics(Attachable data) {
        this.data = data;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        switch (data.getPlaceRequirement()) {
            case ATTACHED_BLOCK_SIDE:
                if (against == BlockFace.SELF || against == BlockFace.DOWN || against == BlockFace.UP) return false;
                return block.getRelative(against.getOppositeFace()).getTypeId() != BlockID.AIR;
            case BLOCK_BELOW:
                return block.getWorld().getBlockTypeIdAt(block.getX(), block.getY(), block.getZ()) != 0;
            case ANYWHERE:
            default:
                return true;
        }
    }
    
    @Override
    public int getPlacedMetadata(int current, BlockFace against) {
        return data.setAttachedFace(current, against);
    }
}

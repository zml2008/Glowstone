package net.glowstone.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockChangeMessage;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import java.io.Serializable;

/**
 * Represents a single block in a world.
 */
public class GlowBlock implements Block, SpoutBlock {

    private GlowChunk chunk;
    private int x;
    private int y;
    private int z;

    public GlowBlock(GlowChunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Basic getters

    public GlowWorld getWorld() {
        return chunk.getWorld();
    }

    public GlowChunk getChunk() {
        return chunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public GlowBlockState getState() {
        if (chunk.getEntity(x & 0xf, y, z & 0xf) != null) {
            return chunk.getEntity(x & 0xf, y, z & 0xf).shallowClone();
        }
        return new GlowBlockState(this);
    }

    public Biome getBiome() {
        return getWorld().getBiome(x, z);
    }

    public double getTemperature() {
        return getWorld().getTemperature(x, z);
    }

    public double getHumidity() {
        return getWorld().getHumidity(x, z);
    }

    // getFace & getRelative

    public BlockFace getFace(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (    (x + face.getModX() == block.getX()) &&
                    (y + face.getModY() == block.getY()) &&
                    (z + face.getModZ() == block.getZ())    ) {
                return face;
            }
        }
        return null;
    }

    public GlowBlock getFace(BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }

    public GlowBlock getFace(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public GlowBlock getRelative(int modX, int modY, int modZ) {
        return getWorld().getBlockAt(x + modX, y + modY, z + modZ);
    }

    public GlowBlock getRelative(BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }

    public Block getRelative(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }
    
    // type and typeid getters/setters

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public int getTypeId() {
        return chunk.getType(x & 0xf, z & 0xf, y & 0x7f);
    }

    public void setType(Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(int type) {
        return setTypeId(type, true);
    }

    public boolean setTypeId(int type, boolean applyPhysics) {
        return setTypeIdAndData(type, (byte) 0, applyPhysics);
    }

    public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics) {
        chunk.setType(x & 0xf, z & 0xf, y & 0x7f, type);
        chunk.setMetaData(x & 0xf, z & 0xf, y & 0x7f, data);
        
        BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, type, data);
        for (GlowPlayer p : getWorld().getRawPlayers()) {
            p.getSession().send(bcmsg);
        }
        
        return true;
    }

    public boolean isEmpty() {
        return getTypeId() == BlockID.AIR;
    }

    public boolean isLiquid() {
        return getTypeId() == BlockID.WATER || getTypeId() == BlockID.STATIONARY_WATER || getTypeId() == BlockID.LAVA || getTypeId() == BlockID.STATIONARY_LAVA;
    }

    // data and light getters/setters

    public byte getData() {
        return (byte) chunk.getMetaData(x & 0xf, z & 0xf, y & 0x7f);
    }

    public void setData(byte data) {
        setData(data, true);
    }

    public void setData(byte data, boolean applyPhyiscs) {
        chunk.setMetaData(x & 0xf, z & 0xf, y & 0x7f, data);
        
        BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, getTypeId(), data);
        for (GlowPlayer p : getWorld().getRawPlayers()) {
            p.getSession().send(bcmsg);
        }
    }

    public byte getLightLevel() {
        return (byte) Math.max(chunk.getSkyLight(x & 0xf, z & 0xf, y & 0x7f), chunk.getBlockLight(x & 0xf, z & 0xf, y & 0x7f));
    }

    // redstone-related shenanigans
    // currently not implemented

    public boolean isBlockPowered() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockIndirectlyPowered() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockFacePowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getBlockPower(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getBlockPower() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PistonMoveReaction getPistonMoveReaction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // ==== Spout ====

    public void setTypeAsync(Material type) {
        setTypeIdAsync(type.getId());
    }

    public void setTypeIdAsync(int type) {
        setTypeIdAndDataAsync(type, (byte)0);
    }

    public void setDataAsync(byte data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTypeIdAndDataAsync(int type, byte data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCustomBlock(CustomBlock block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable setData(String id, Serializable data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable getData(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable removeData(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        return MaterialData.getMaterial(getTypeId(), getData()).getName();
    }

    public void setBlockPowered(boolean power) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBlockPowered(boolean power, BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetBlockPower() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public org.getspout.spoutapi.material.Block getBlockType() {
        return MaterialData.getBlock(getTypeId(), getData());
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public ItemStack toItemStack(int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer getCustomBlockId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer getCustomMetaData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCustomBlock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeCustomBlockData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CustomBlock getCustomBlock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

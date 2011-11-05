package net.glowstone.spout;

import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.getspout.spoutapi.chunkdatamanager.ChunkDataManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.io.Serializable;

public class GlowChunkDataManager implements ChunkDataManager, GlowSpoutComponent {
    
    public int getStringId(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable setBlockData(String id, World world, int x, int y, int z, Serializable data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable getBlockData(String id, World world, int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable removeBlockData(String id, World world, int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable setChunkData(String id, World world, int x, int z, Serializable data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable getChunkData(String id, World world, int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Serializable removeChunkData(String id, World world, int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BlockVector[] getTaggedBlocks(World world, int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void registerPlayer(SpoutPlayer player) {}

    public void resetAll() {}
}
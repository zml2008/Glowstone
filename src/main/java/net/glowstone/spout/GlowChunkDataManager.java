package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;
import org.getspout.spoutapi.chunkdatamanager.ChunkDataManager;
import org.getspout.spoutapi.chunkstore.SimpleChunkDataManager;


public class GlowChunkDataManager extends SimpleChunkDataManager implements ChunkDataManager, GlowSpoutComponent {

    public void registerPlayer(GlowPlayer player) {}

    public void resetAll() {}
}
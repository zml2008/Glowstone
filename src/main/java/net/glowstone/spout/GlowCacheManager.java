package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.LoadChunkMessage;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkcache.CacheManager;

public class GlowCacheManager implements CacheManager, GlowSpoutComponent {

    public void handle(int id, boolean add, long[] hashes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refreshChunkRequest(int id, int cx, int cz) {
        GlowPlayer player = (GlowPlayer) SpoutManager.getPlayerFromId(id);
        player.getSession().send(new LoadChunkMessage(cx, cz, false));
        player.getSession().send(new LoadChunkMessage(cx, cz, true));
        player.getSession().send(player.getWorld().getChunkAt(cx, cz).toMessage());
    }

    public void registerPlayer(GlowPlayer player) {}

    public void resetAll() {}
}

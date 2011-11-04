package net.glowstone.spout;

import org.getspout.spoutapi.chunkcache.CacheManager;

public class GlowCacheManager implements CacheManager {

    public void handle(int id, boolean add, long[] hashes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refreshChunkRequest(int id, int cx, int cz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

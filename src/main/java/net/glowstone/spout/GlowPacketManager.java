package net.glowstone.spout;

import java.util.Arrays;
import java.util.ArrayList;

import net.glowstone.entity.GlowPlayer;
import org.getspout.spoutapi.packet.PacketManager;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

/**
 * Packet manager for Spout integration.
 */
public class GlowPacketManager implements PacketManager, GlowSpoutComponent {
    
    private static final int UNCOMPRESSED_ID = -1;
    private ArrayList<PacketListener>[] listeners = new ArrayList[256];
    
    // add

    public void addListener(int packetId, PacketListener listener) {
        getList(packetId).add(listener);
    }

    public void addListenerUncompressedChunk(PacketListener listener) {
        getList(UNCOMPRESSED_ID).add(listener);
    }
    
    // remove

    public boolean removeListener(int packetId, PacketListener listener) {
        return getList(packetId).remove(listener);
    }

    public boolean removeListenerUncompressedChunk(PacketListener listener) {
        return getList(UNCOMPRESSED_ID).remove(listener);
    }

    public void clearAllListeners() {
        Arrays.fill(listeners, new ArrayList<PacketListener>());
    }
    
    // misc

    public MCPacket getInstance( final int packetId) {
        throw new UnsupportedOperationException("Glowstone's packet system doesn't work like this! (Actually zml is just too lazy to make this work).");
    }
    
    private ArrayList<PacketListener> getList(int packetId) {

         if (listeners.length > packetId) {
                if (listeners[packetId] == null) {
                    return listeners[packetId] = new ArrayList<PacketListener>();
                } else {
                    return listeners[packetId];
                }
        } else {
            listeners = Arrays.copyOf(listeners, packetId + 2);
            return listeners[packetId] = new ArrayList<PacketListener>();
        }
    }

    public void registerPlayer(GlowPlayer player) {}

    public void resetAll() {
        clearAllListeners();
    }
}

package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.AnimateEntityMessage;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkstore.PlayerTrackingManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for managing Spout integration.
 */
public class GlowSpoutManager {
    private static PlayerTrackingManager tracking = new PlayerTrackingManager();
    
    private GlowSpoutManager() {}
    private static final Set<GlowSpoutComponent> components = new HashSet<GlowSpoutComponent>();
    
    static {
        SpoutManager.getInstance().setAppearanceManager(register(new GlowAppearanceManager()));
        SpoutManager.getInstance().setInventoryBuilder(register(new GlowInventoryBuilder()));
        SpoutManager.getInstance().setItemManager(register(new GlowItemManager()));
        SpoutManager.getInstance().setKeyboardManager(register(new GlowKeyboardManager()));
        SpoutManager.getInstance().setPacketManager(register(new GlowPacketManager()));
        SpoutManager.getInstance().setPlayerManager(register(new GlowPlayerManager()));
        SpoutManager.getInstance().setSkyManager(register(new GlowSkyManager()));
        SpoutManager.getInstance().setSoundManager(register(new GlowSoundManager()));
        SpoutManager.getInstance().setBiomeManager(register(new GlowBiomeManager()));
        SpoutManager.getInstance().setCacheManager(register(new GlowCacheManager()));
        SpoutManager.getInstance().setChunkDataManager(register(new GlowChunkDataManager()));
        SpoutManager.getInstance().setFileManager(register(new GlowFileManager()));
        SpoutManager.getInstance().setKeyBindingManager(register(new GlowKeyBindingManager()));
        SpoutManager.getInstance().setMaterialManager(register(new GlowMaterialManager()));
    }

    private static <T extends GlowSpoutComponent> T register(T component) {
        components.add(component);
        return component;
    }
    
    /**
     * Register a player join with the appropriate managers.
     * @param player The player to register.
     */
    public static void registerPlayer(GlowPlayer player) {
        for (GlowSpoutComponent component : components) {
            component.registerPlayer(player);
        }
        
        // send the magic animate packet
        player.getSession().send(new AnimateEntityMessage(-42, 0));
    }
    
    /**
     * Reset the states of all managers to their default.
     */
    public static void resetAll() {
        for (GlowSpoutComponent component: components) {
            component.resetAll();
        }
    }

    public static PlayerTrackingManager getTrackingManager() {
        return tracking;
    }
    
}

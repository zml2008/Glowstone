package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.AnimateEntityMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketAllowVisualCheats;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for managing Spout integration.
 */
public class GlowSpoutManager {
    
    private GlowSpoutManager() {}
    private static final Set<GlowSpoutComponent> components = new HashSet<GlowSpoutComponent>();
    
    public static final int verMajor = 1;
    public static final int verMinor = 0;
    public static final int verBuild = 1;
    
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
    public static void registerPlayer(SpoutPlayer player) {
        for (GlowSpoutComponent component : components) {
            component.registerPlayer(player);
        }
        
        // send the magic animate packet
        ((GlowPlayer) player).getSession().send(new AnimateEntityMessage(-42, 0));
    }
    
    /**
     * Reset the states of all managers to their default.
     */
    public static void resetAll() {
        for (GlowSpoutComponent component: components) {
            component.resetAll();
        }
    }
    
}

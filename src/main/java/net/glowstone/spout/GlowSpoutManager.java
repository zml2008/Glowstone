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

/**
 * Helper class for managing Spout integration.
 */
public class GlowSpoutManager {
    
    private GlowSpoutManager() {}
    
    private static final GlowAppearanceManager appearance = new GlowAppearanceManager();
    private static final GlowInventoryBuilder inventory = new GlowInventoryBuilder();
    private static final GlowItemManager items = new GlowItemManager();
    private static final GlowKeyboardManager keyboard = new GlowKeyboardManager();
    private static final GlowPacketManager packets = new GlowPacketManager();
    private static final GlowPlayerManager players = new GlowPlayerManager();
    private static final GlowSkyManager sky = new GlowSkyManager();
    private static final GlowSoundManager sound = new GlowSoundManager();
    private static final GlowBiomeManager biomes = new GlowBiomeManager();
    private static final GlowCacheManager cache = new GlowCacheManager();
    private static final GlowChunkDataManager chunkData = new GlowChunkDataManager();
    private static final GlowFileManager files = new GlowFileManager();
	private static final GlowKeyBindingManager keyBindings = new GlowKeyBindingManager();
	private static final GlowMaterialManager materials = new GlowMaterialManager();
    
    public static final int verMajor = 1;
    public static final int verMinor = 0;
    public static final int verBuild = 1;
    
    static {
        SpoutManager.getInstance().setAppearanceManager(appearance);
        SpoutManager.getInstance().setInventoryBuilder(inventory);
        SpoutManager.getInstance().setItemManager(items);
        SpoutManager.getInstance().setKeyboardManager(keyboard);
        SpoutManager.getInstance().setPacketManager(packets);
        SpoutManager.getInstance().setPlayerManager(players);
        SpoutManager.getInstance().setSkyManager(sky);
        SpoutManager.getInstance().setSoundManager(sound);
        SpoutManager.getInstance().setBiomeManager(biomes);
        SpoutManager.getInstance().setCacheManager(cache);
        SpoutManager.getInstance().setChunkDataManager(chunkData);
        SpoutManager.getInstance().setFileManager(files);
        SpoutManager.getInstance().setKeyBindingManager(keyBindings);
        SpoutManager.getInstance().setMaterialManager(materials);
    }
    
    /**
     * Register a player join with the appropriate managers.
     * @param player The player to register.
     */
    public static void registerPlayer(SpoutPlayer player) {
        appearance.registerPlayer(player);
        items.registerPlayer(player);
        sky.registerPlayer(player);
        
        // send the magic animate packet
        ((GlowPlayer) player).getSession().send(new AnimateEntityMessage(-42, 0));
    }
    
    /**
     * Reset the states of all managers to their default.
     */
    public static void resetAll() {
        appearance.resetAll();
        items.reset();
        packets.clearAllListeners();
        sky.resetAll();
    }
    
}

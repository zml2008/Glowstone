package net.glowstone.spout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.inventory.ItemManager;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.packet.PacketItemName;

/**
 * Item manager for Spout integration. Adapted from SimpleItemManager from the
 * Spout plugin for CraftBukkit, used with permission.
 */
@Deprecated
public class GlowItemManager implements ItemManager, GlowSpoutComponent {

    private final HashMap<ItemData, String> itemNames;
    private final HashMap<ItemData, String> customNames;

    // constructor
    
    public GlowItemManager() {
        itemNames = new HashMap<ItemData, String>(500);
        customNames = new HashMap<ItemData, String>(100);
    }

    // get

    public String getItemName(Material item) {
        return getItemName(item, (short) 0);
    }

    public String getItemName(Material item, short data) {
        ItemData info = new ItemData(item.getId(), data);
        return customNames.containsKey(info) ? customNames.get(info) : itemNames.get(info);
    }

    public String getCustomItemName(Material item) {
        return getCustomItemName(item, (short) 0);
    }

    public String getCustomItemName(Material item, short data) {
        ItemData info = new ItemData(item.getId(), data);
        return customNames.get(info);
    }

    // set
    
    public void setItemName(Material item, String name) {
        setItemName(item, (short) 0, name);
    }

    public void setItemName(Material item, short data, String name) {
        customNames.put(new ItemData(item.getId(), data), name);
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            SpoutManager.getPlayer(player).sendPacket(new PacketItemName(item.getId(), (short) 0, "[reset]"));
        }
    }
    // reset

    public void resetName(Material item) {
        resetName(item, (byte) 0);
    }

    public void resetName(Material item, short data) {
        ItemData info = new ItemData(item.getId(), data);
        if (customNames.containsKey(info)) {
            customNames.remove(info);
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                SpoutManager.getPlayer(player).sendPacket(new PacketItemName(info.id, info.data, "[reset]"));
            }
        }
    }

    // Stuff added since this was last updated, but before Glowstone was updated. MaterialManager should be used instead.

    public String getItemName(int item, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setItemName(int item, short data, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemName(CustomItem item, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setItemTexture(Material item, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemTexture(Material item, Plugin plugin, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemTexture(Material item, short data, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemTexture(int id, short data, Plugin plugin, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemTexture(CustomItem item, Plugin plugin, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexture(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexturePlugin(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexture(Material item, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexturePlugin(Material item, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetTexture(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetTexture(Material item, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int registerCustomItemName(Plugin plugin, String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getCustomItemId(Plugin plugin, String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCustomItemBlock(CustomItem item, CustomBlock block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ItemStack getCustomItemStack(CustomBlock block, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ItemStack getCustomItemStack(CustomItem item, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean overrideBlock(org.bukkit.block.Block block, CustomBlock customBlock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean overrideBlock(World world, int x, int y, int z, CustomBlock customBlock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCustomBlockDesign(int blockId, short metaData, BlockDesign design) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCustomBlock(org.bukkit.block.Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SpoutBlock getSpoutBlock(org.bukkit.block.Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean registerSpoutRecipe(Recipe recipe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCustomItem(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CustomItem getCustomItem(ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CustomBlock registerItemDrop(CustomBlock block, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasItemDrop(CustomBlock block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ItemStack getItemDrop(CustomBlock block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getStepSound(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStepSound(int id, short data, String url) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetStepSound(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getFriction(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFriction(int id, short data, float friction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetFriction(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getHardness(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setHardness(int id, short data, float hardness) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetHardness(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isOpaque(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOpaque(int id, short data, boolean opacity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetOpacity(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLightLevel(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLightLevel(int id, short data, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetLightLevel(int id, short data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Block> getModifiedBlocks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // helpers

    public void reset() {
        customNames.clear();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            SpoutManager.getPlayer(player).sendPacket(new PacketItemName(0, (short) 0, "[resetall]"));
        }
    }
    
    public void registerPlayer(GlowPlayer player) {
        if (player.isSpoutCraftEnabled()) {
            Iterator<Entry<ItemData, String>> i = customNames.entrySet().iterator();
            while (i.hasNext()) {
                Entry<ItemData, String> entry = i.next();
                player.sendPacket(new PacketItemName(entry.getKey().id, entry.getKey().data, entry.getValue()));
            }
        }
    }

    public void resetAll() {
        reset();
    }

    private static class ItemData {
        public final int id;
        public final short data;

        public ItemData(int id) {
            this(id, 0);
        }

        public ItemData(int id, int data) {
            this.id = id;
            this.data = (short) data;
        }
    }
    
}

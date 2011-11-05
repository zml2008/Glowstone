package net.glowstone.spout;

import net.glowstone.block.GlowBlock;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.inventory.MaterialManager;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.Set;

public class GlowMaterialManager implements MaterialManager, GlowSpoutComponent {


    public String getStepSound(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStepSound(Block block, String url) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetStepSound(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getFriction(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFriction(Block block, float friction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetFriction(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getHardness(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setHardness(Block block, float hardness) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetHardness(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isOpaque(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOpaque(Block block, boolean opacity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetOpacity(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLightLevel(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLightLevel(Block block, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetLightLevel(Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Block> getModifiedBlocks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onCustomMaterialRegistered(Material mat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemName(Material item, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetName(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setItemTexture(Material item, Plugin plugin, String texture) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexture(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCustomItemTexturePlugin(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetTexture(Material item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int registerCustomItemName(Plugin plugin, String key) {
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

    public boolean removeBlockOverride(org.bukkit.block.Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean overrideBlock(org.bukkit.block.Block block, CustomBlock customBlock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean overrideBlock(World world, int x, int y, int z, CustomBlock customBlock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCustomBlockDesign(Material material, BlockDesign design) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCustomBlock(org.bukkit.block.Block block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GlowBlock getSpoutBlock(org.bukkit.block.Block block) {
        if (block instanceof GlowBlock) {
            return (GlowBlock) block;
        }
        throw new UnsupportedOperationException("Block " + block + " is not a GlowBlock!");
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

    public void registerPlayer(SpoutPlayer player) {}

    public void resetAll() {}
}

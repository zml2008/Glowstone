package net.glowstone.worldedit;

import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.ServerInterface;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Player;

/**
 * @author zml2008
 */
public class GlowWEPlayer extends BukkitPlayer {

    public GlowWEPlayer(WorldEditPlugin plugin, ServerInterface server, Player player) {
        super(plugin, server, player);
    }

    @Override
    public LocalWorld getWorld() {
        return new GlowWEWorld((GlowWorld) getPlayer().getWorld());
    }
}

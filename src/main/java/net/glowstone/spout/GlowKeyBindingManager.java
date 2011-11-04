package net.glowstone.spout;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.UUID;

public class GlowKeyBindingManager implements KeyBindingManager {


    public void registerBinding(String id, Keyboard defaultKey, String description, BindingExecutionDelegate callback, Plugin plugin) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void summonKey(UUID uniqueId, SpoutPlayer player, Keyboard key, boolean pressed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

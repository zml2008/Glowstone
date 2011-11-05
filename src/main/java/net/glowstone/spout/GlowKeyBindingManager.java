package net.glowstone.spout;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.KeyBinding;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.PacketKeyBinding;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlowKeyBindingManager implements KeyBindingManager, GlowSpoutComponent {
    private final Map<UUID, KeyBinding> registeredBindings = new HashMap<UUID, KeyBinding>();


    public void registerBinding(String id, Keyboard defaultKey, String description, BindingExecutionDelegate callback, Plugin plugin) throws IllegalArgumentException {
        if (searchBinding(id, plugin) != null) {
            throw new IllegalArgumentException("Key binding is already defined as " + id + " for plugin " + plugin.getDescription().getName());
        }
        KeyBinding binding = new KeyBinding(id, defaultKey, description, plugin, callback);
        registeredBindings.put(binding.getUniqueId(), binding);
        PacketKeyBinding packet = new PacketKeyBinding(binding);
        for (SpoutPlayer player : SpoutManager.getPlayerManager().getOnlinePlayers()) {
            player.sendPacket(packet);
        }
    }

    public void summonKey(UUID uniqueId, SpoutPlayer player, Keyboard key, boolean pressed) {
        KeyBinding binding = registeredBindings.get(uniqueId);
        if (binding == null) {
            return;
        }
        if (pressed) {
            binding.getDelegate().keyPressed(new KeyBindingEvent(player, binding));
        } else {
            binding.getDelegate().keyReleased(new KeyBindingEvent(player, binding));
        }
    }
    
    public KeyBinding searchBinding(String id, Plugin plugin) {
        for (KeyBinding binding : registeredBindings.values()) {
            if (binding.getId().equals(id) && binding.getPlugin().equals(plugin))
            return binding;
        }
        return null;
    }

    public void registerPlayer(SpoutPlayer player) {
        System.out.println("Registering player: " + player);
        for (KeyBinding binding : registeredBindings.values()) {
            player.sendPacket(new PacketKeyBinding(binding));
        }
    }

    public void resetAll() {}
}

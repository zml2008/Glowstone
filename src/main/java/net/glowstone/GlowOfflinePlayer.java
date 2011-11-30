package net.glowstone;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public class GlowOfflinePlayer implements OfflinePlayer, Permissible {

    private final GlowServer server;
    private final String name;

    protected PermissibleBase permissions = new PermissibleBase(this);

    public GlowOfflinePlayer(GlowServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public boolean isOnline() {
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean isBanned() {
        return server.getBanManager().isBanned(name);
    }

    public void setBanned(boolean banned) {
        server.getBanManager().setBanned(name, banned);
    }

    public boolean isWhitelisted() {
        return server.hasWhitelist() && server.getWhitelist().contains(name);
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(name);
        } else {
            server.getWhitelist().remove(name);
        }
    }

    public Player getPlayer() {
        return server.getPlayerExact(name);
    }

    public boolean isOp() {
        return server.getOpsList().contains(name);
    }

    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(name);
        } else {
            server.getOpsList().remove(name);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();

        ret.put("name", name);
        return ret;
    }

    public static OfflinePlayer deserialize(Map<String, Object> val) {
        return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
    }

    public boolean isPermissionSet(String name) {
        return permissions.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return permissions.isPermissionSet(perm);
    }
    public boolean hasPermission(String name) {
        return permissions.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return permissions.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return permissions.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return permissions.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return permissions.addAttachment(plugin, name, value, ticks);
    }
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return permissions.addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        permissions.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        permissions.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return permissions.getEffectivePermissions();
    }
}

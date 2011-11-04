package net.glowstone.spout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.PlayerManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.GlowServer;

/**
 * Player manager for Spout integration.
 */
public class GlowPlayerManager implements PlayerManager {
    
    private final HashSet<Player> nagList = new HashSet<Player>();
    private final PlayerInformation globalInfo = new GlowPlayerInformation();

	public SpoutPlayer getPlayer(Player player) {
        if (player == null) {
            throw new NullPointerException();
        }
        
		if (player instanceof GlowPlayer) {
            // all good
            return (GlowPlayer) player;
        } else if (player instanceof SpoutPlayer) {
            // should never happen, but...
            if (!nagList.contains(player)) {
                GlowServer.logger.log(Level.SEVERE, "SpoutPlayer {0} was not a GlowPlayer, instead {1}", new Object[]{player.getName(), player.getClass().getName()});
                nagList.add(player);
            }
            return (SpoutPlayer) player;
        } else {
            // should never happen!
            if (!nagList.contains(player)) {
                GlowServer.logger.log(Level.SEVERE, "Player {0} was not a GlowPlayer, instead {1}", new Object[]{player.getName(), player.getClass().getName()});
                nagList.add(player);
            }
            throw new IllegalStateException("Player was not a GlowPlayer");
        }
	}

    public GlowPlayer getPlayer(SpoutPlayer player) {
        if (player instanceof GlowPlayer) {
            return (GlowPlayer) player;
        }
        throw new IllegalStateException("SpoutPlayer was not a GlowPlayer");
    }

	public SpoutPlayer getPlayer(UUID id) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getUniqueId().equals(id)) {
				return getPlayer(player);
			}
		}
		return null;
	}

	public SpoutPlayer getPlayer(int entityId) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getEntityId() == entityId) {
				return getPlayer(player);
			}
		}
		return null;
	}

    public PlayerInformation getPlayerInfo(Player player) {
        return getPlayer(player).getInformation();
    }

    public PlayerInformation getGlobalInfo() {
        return globalInfo;
    }

    public SpoutPlayer[] getOnlinePlayers() {
        List<SpoutPlayer> players = new ArrayList<SpoutPlayer>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            players.add(getPlayer(player));
        }
        return players.toArray(new SpoutPlayer[players.size()]);
    }

    public void setVersionString(int playerId, String versionString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Entity getEntity(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Entity getEntity(int entityId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

package net.glowstone.spout;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.packet.PacketBiomeWeather;
import org.getspout.spoutapi.player.BiomeManager;
import org.getspout.spoutapi.player.SpoutPlayer;


public class GlowBiomeManager implements BiomeManager {

    public void setPlayerBiomeWeather(SpoutPlayer player, Biome biome, SpoutWeather weather) {
        if (player.isSpoutCraftEnabled()) {
            player.getInformation().setBiomeWeather(biome, weather);
            player.sendPacket(new PacketBiomeWeather(biome, weather));
        }
    }

    public void setPlayerWeather(SpoutPlayer player, SpoutWeather weather) {
        if (player.isSpoutCraftEnabled()) {
            for (Biome biome : Biome.values()) {
                player.getInformation().setBiomeWeather(biome, weather);
                player.sendPacket(new PacketBiomeWeather(biome, weather));
            }
        }
    }

    public void setGlobalBiomeWeather(Biome biome, SpoutWeather weather) {
        SpoutManager.getPlayerManager().getGlobalInfo().setBiomeWeather(biome, weather);
        PacketBiomeWeather packet = new PacketBiomeWeather(biome, weather);
        for (SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
            if (player.isSpoutCraftEnabled()) {
                player.sendPacket(packet);
            }
        }
    }

    public void setGlobalWeather(SpoutWeather weather) {
        for (Biome biome : Biome.values()) {
            setGlobalBiomeWeather(biome, weather);
        }
    }

    public SpoutWeather getGlobalBiomeWeather(Biome biome) {
        return SpoutManager.getPlayerManager().getGlobalInfo().getBiomeWeather(biome);
    }

    public SpoutWeather getPlayerBiomeWeather(Player player, Biome biome) {
        return SpoutManager.getPlayer(player).getInformation().getBiomeWeather(biome);
    }

    
}

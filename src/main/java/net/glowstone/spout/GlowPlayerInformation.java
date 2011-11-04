package net.glowstone.spout;

import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.player.EntitySkinType;
import org.getspout.spoutapi.player.PlayerInformation;

import java.util.Set;

public class GlowPlayerInformation implements PlayerInformation {

    public SpoutWeather getBiomeWeather(Biome biome) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBiomeWeather(Biome biome, SpoutWeather weather) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Biome> getBiomes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEntitySkin(LivingEntity entity, String url) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEntitySkin(LivingEntity entity, String url, EntitySkinType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getEntitySkin(LivingEntity entity, EntitySkinType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

package net.glowstone.spout;

import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.player.EntitySkinType;
import org.getspout.spoutapi.player.PlayerInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlowPlayerInformation implements PlayerInformation {
    private Map<Biome, SpoutWeather> weather = new HashMap<Biome, SpoutWeather>();

    public SpoutWeather getBiomeWeather(Biome biome) {
        return weather.get(biome);
    }

    public void setBiomeWeather(Biome biome, SpoutWeather weather) {
        this.weather.put(biome, weather);
    }

    public Set<Biome> getBiomes() {
        return weather.keySet();
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

package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

public interface GlowSpoutComponent {
    public void registerPlayer(SpoutPlayer player);

    public void resetAll();
}

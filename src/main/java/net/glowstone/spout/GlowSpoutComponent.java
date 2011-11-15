package net.glowstone.spout;

import net.glowstone.entity.GlowPlayer;

public interface GlowSpoutComponent {
    public void registerPlayer(GlowPlayer player);

    public void resetAll();
}

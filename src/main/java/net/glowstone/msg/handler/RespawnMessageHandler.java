package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.RespawnMessage;
import net.glowstone.net.Session;

public class RespawnMessageHandler extends MessageHandler<RespawnMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, RespawnMessage message) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.setHealth(20);
    }
}

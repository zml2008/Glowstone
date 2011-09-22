package net.glowstone.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.glowstone.EventFactory;
import net.glowstone.GlowServer;

import net.glowstone.msg.EntityEffectMessage;
import net.glowstone.msg.EntityRemoveEffectMessage;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;

import net.glowstone.util.Parameter;
import net.glowstone.util.Position;
import net.glowstone.msg.EntityRotationMessage;
import net.glowstone.msg.EntityTeleportMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.RelativeEntityPositionMessage;
import net.glowstone.msg.RelativeEntityPositionRotationMessage;
import net.glowstone.GlowWorld;

/**
 * A GlowLivingEntity is a {@link Player} or {@link org.bukkit.entity.Monster}.
 * @author Graham Edgecombe.
 */
public abstract class GlowLivingEntity extends GlowEntity implements LivingEntity {
    
    /**
     * The entity's health.
     */
    protected int health = 0;

    /**
     * The monster's metadata.
     */
    protected final List<Parameter<?>> metadata = new ArrayList<Parameter<?>>();

    /**
     * The entity's active effects
     */
    private Set<ActiveEntityEffect> activeEffects = Collections.synchronizedSet(new HashSet<ActiveEntityEffect>());

    /**
     * Creates a mob within the specified world.
     * @param world The world.
     */
    public GlowLivingEntity(GlowServer server, GlowWorld world) {
        super(server, world);
    }

    @Override
    public void pulse() {
        for (Iterator<ActiveEntityEffect> i = activeEffects.iterator(); i.hasNext();) {
            ActiveEntityEffect effect = i.next();
            if (!effect.pulse()) removeEntityEffect(effect);
        }
    }

    @Override
    public Message createUpdateMessage() {
        boolean moved = hasMoved();
        boolean rotated = hasRotated();

        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int dx = x - Position.getIntX(previousLocation);
        int dy = y - Position.getIntY(previousLocation);
        int dz = z - Position.getIntZ(previousLocation);

        boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

        int yaw = Position.getIntYaw(previousLocation);
        int pitch = Position.getIntPitch(previousLocation);

        if (moved && teleport) {
            return new EntityTeleportMessage(id, x, y, z, yaw, pitch);
        } else if (moved && rotated) {
            return new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch);
        } else if (moved) {
            return new RelativeEntityPositionMessage(id, dx, dy, dz);
        } else if (rotated) {
            return new EntityRotationMessage(id, yaw, pitch);
        }

        return null;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) health = 0;
        if (health > 200) health = 200;
        this.health = health;
    }

    public double getEyeHeight() {
       return getEyeHeight(false);
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        if (false /* TODO: sneaking */ || !ignoreSneaking) {
            return 1.6;
        } else {
            return 1.4;
        }
    }

    public Location getEyeLocation() {
        Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Egg throwEgg() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Snowball throwSnowball() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Arrow shootArrow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInsideVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean leaveVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Vehicle getVehicle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getRemainingAir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRemainingAir(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaximumAir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMaximumAir(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void damage(int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void damage(int amount, Entity source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaximumNoDamageTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMaximumNoDamageTicks(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLastDamage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLastDamage(int damage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNoDamageTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNoDamageTicks(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected Parameter<?> getMetadata(int index) {
        return metadata.get(index);
    }

    protected void setMetadata(Parameter<?> data) {
        if(data.getIndex() < metadata.size()) {
            metadata.set(data.getIndex(), data);
        } else {
            metadata.add(data);
        }
    }

    public void addEntityEffect(EntityEffect effect, byte amplitude, short duration) {
        addEntityEffect(new ActiveEntityEffect(effect, amplitude, duration));
    }

    public void addEntityEffect(ActiveEntityEffect effect) {
        if (EventFactory.onEntityAddEffect(this, effect).isCancelled()) return;
        activeEffects.add(effect);
        EntityEffectMessage msg = new EntityEffectMessage(getEntityId(), effect.getEffect().getId(), effect.getAmplitude(), effect.getDuration());
        for (GlowPlayer player : world.getRawPlayers()) {
            if (player.canSee(this)) player.getSession().send(msg);
        }
    }

    public void removeEntityEffect(ActiveEntityEffect effect) {
        EventFactory.onEntityRemoveEffect(this, effect.getEffect());
        activeEffects.remove(effect);
        EntityRemoveEffectMessage msg = new EntityRemoveEffectMessage(getEntityId(), effect.getEffect().getId());
        for (GlowPlayer player : world.getRawPlayers()) {
            if (player.canSee(this)) player.getSession().send(msg);
        }
    }

}

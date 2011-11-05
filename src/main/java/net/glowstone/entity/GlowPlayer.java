package net.glowstone.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.block.GlowBlockState;
import net.glowstone.io.StorageOperation;
import net.glowstone.msg.*;
import net.glowstone.spout.GlowPlayerInformation;
import net.glowstone.util.Position;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerChatEvent;

import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.OverlayScreen;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.io.CRCStore;
import org.getspout.spoutapi.io.CRCStoreRunnable;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.RenderDistance;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.*;
import org.getspout.spoutapi.packet.standard.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import net.glowstone.EventFactory;
import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.inventory.InventoryViewer;
import net.glowstone.util.Parameter;
import net.glowstone.util.TextWrapper;
import net.glowstone.net.Session;
import org.bukkit.map.MapView;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
@DelegateDeserialization(GlowOfflinePlayer.class)
public final class GlowPlayer extends GlowHumanEntity implements Player, SpoutPlayer, InventoryViewer {

    /**
     * The normal height of a player's eyes above their feet.
     */
    public static final double EYE_HEIGHT = 1.62D;

    /**
     * This player's session.
     */
    private final Session session;
    
    /**
     * Cumulative amount of experience points the player has collected.
     */
    private int experience = 0;
    
    /**
     * The current level (or skill point amount) of the player.
     */
    private int level = 0;
    
    /**
     * The player's current exhaustion level.
     */
    private float exhaustion = 0;
    
    /**
     * The player's current saturation level.
     */
    private float saturation = 0;
    
    /**
     * This player's current time offset.
     */
    private long timeOffset = 0;
    
    /**
     * Whether the time offset is relative.
     */
    private boolean timeRelative = true;
    
    /**
     * The display name of this player, for chat purposes.
     */
    private String displayName;
    
    /**
     * The player's compass target.
     */
    private Location compassTarget;

    /**
     * The entities that the client knows about.
     */
    private Set<GlowEntity> knownEntities = new HashSet<GlowEntity>();

    /**
     * The chunks that the client knows about.
     */
    private final Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();
    
    /**
     * The item the player has on their cursor.
     */
    private ItemStack itemOnCursor;

    /**
     * Whether the player is sneaking.
     */
    private boolean sneaking = false;

    /**
     * The human entity's current food level
     */
    private int food = 20;

    /**
     * Whether to use the display name when sending spawn messages to 
     */
    private boolean dispNameAsEntityName = false;

    /**
     * The bed spawn location of a player
     */
    private Location bedSpawn;

    /**
     * The name a player has in the player list
     */
    private String playerListName;

    /**
     * This player's Spout information
     */
    private GlowPlayerInformation information = new GlowPlayerInformation();

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
    public GlowPlayer(Session session, String name) {
        super(session.getServer(), (GlowWorld) session.getServer().getWorlds().get(0), name);
        this.session = session;
        health = 20;
        if (session.getState() != Session.State.GAME) {
            session.send(new IdentificationMessage(getEntityId(), "", world.getSeed(), getGameMode().getValue(), world.getEnvironment().getId(), 1, world.getMaxHeight(), session.getServer().getMaxPlayers()));
        }
        streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2), (byte)0)); // send the world's weather

        getInventory().addViewer(this);
        getInventory().getCraftingInventory().addViewer(this);

        loadData();
        saveData();
    }
    
    // -- Various internal mechanisms

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    @Override
    public void remove() {
        saveData();
        getInventory().removeViewer(this);
        getInventory().getCraftingInventory().removeViewer(this);
        super.remove();
    }

    @Override
    public void pulse() {
        super.pulse();

        streamBlocks();

        for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
            GlowEntity entity = it.next();
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance) {
                Message msg = entity.createUpdateMessage();
                if (msg != null)
                    session.send(msg);
            } else {
                session.send(new DestroyEntityMessage(entity.getEntityId()));
                it.remove();
            }
        }

        for (GlowEntity entity : world.getEntityManager()) {
            if (entity == this)
                continue;
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance && !knownEntities.contains(entity)) {
                knownEntities.add(entity);
                session.send(entity.createSpawnMessage());
            }
        }
        
        // Spout
        spoutcraft.inGameScreen.onTick();
        if (spoutcraft.screen != null && spoutcraft.screen instanceof OverlayScreen) {
            spoutcraft.screen.onTick();
        }
    }

    /**
     * Streams chunks to the player's client.
     */
    private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<GlowChunk.Key>(knownChunks);
        ArrayList<GlowChunk.Key> newChunks = new ArrayList<GlowChunk.Key>();

        int centralX = ((int) location.getX()) >> 4;
        int centralZ = ((int) location.getZ()) >> 4;
        
        int radius = server.getViewDistance();
        for (int x = (centralX - radius); x <= (centralX + radius); x++) {
            for (int z = (centralZ - radius); z <= (centralZ + radius); z++) {
                GlowChunk.Key key = new GlowChunk.Key(x, z);
                if (!knownChunks.contains(key)) {
                    knownChunks.add(key);
                    newChunks.add(key);
                }
                previousChunks.remove(key);
            }
        }
        
        Collections.sort(newChunks, new Comparator<GlowChunk.Key>() {
            public int compare(GlowChunk.Key a, GlowChunk.Key b) {
                double dx = 16 * a.getX() + 8 - location.getX();
                double dz = 16 * a.getZ() + 8 - location.getZ();
                double da = dx * dx + dz * dz;
                dx = 16 * b.getX() + 8 - location.getX();
                dz = 16 * b.getZ() + 8 - location.getZ();
                double db = dx * dx + dz * dz;
                return Double.compare(da, db);
            }
        });
        
        for (GlowChunk.Key key : newChunks) {
            session.send(new LoadChunkMessage(key.getX(), key.getZ(), true));
            session.send(world.getChunkAt(key.getX(), key.getZ()).toMessage());
            for (GlowBlockState state : world.getChunkAt(key.getX(), key.getZ()).getTileEntities()) {
                state.update(this);
            }
        }

        for (GlowChunk.Key key : previousChunks) {
            session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            knownChunks.remove(key);
        }

        previousChunks.clear();
    }
    
    /**
     * Checks whether the player can see the given chunk.
     * @return If the chunk is known to the player's client.
     */
    public boolean canSee(GlowChunk.Key chunk) {
        return knownChunks.contains(chunk);
    }
    
    /**
     * Checks whether the player can see the given entity.
     * @return If the entity is known to the player's client.
     */
    public boolean canSee(GlowEntity entity) {
        return knownEntities.contains(entity);
    }
    
    // -- Basic getters

    /**
     * Gets the session.
     * @return The session.
     */
    public Session getSession() {
        return session;
    }

    public boolean isOnline() {
        return true;
    }

    public boolean isBanned() {
        return server.getBanManager().isBanned(getName());
    }

    public void setBanned(boolean banned) {
        server.getBanManager().setBanned(getName(), banned);
    }

    public boolean isWhitelisted() {
        return !server.hasWhitelist() || server.getWhitelist().contains(getName());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(getName());
        } else {
            server.getWhitelist().remove(getName());
        }
    }

    public Player getPlayer() {
        return this;
    }

    public InetSocketAddress getAddress() {
        return session.getAddress();
    }

    @Override
    public boolean isOp() {
        return getServer().getOpsList().contains(getName());
    }
    
    @Override
    public void setOp(boolean value) {
        if (value) {
            getServer().getOpsList().add(getName());
        } else {
            getServer().getOpsList().remove(getName());
        }
        permissions.recalculatePermissions();
    }
    
    // -- Malleable properties

    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public String getPlayerListName() {
        return playerListName == null || "".equals(playerListName) ? getName() : playerListName;
    }

    public void setPlayerListName(String name) {
        if (name.length() > 15) throw new IllegalArgumentException("The given name was " + name.length() + " chars long, longer than the maximum of 16");
        for (Player player : server.getOnlinePlayers()) {
            if (player.getPlayerListName().equals(getPlayerListName())) throw new IllegalArgumentException("The name given, " + name + ", is already used by " + player.getName() + ".");
        }
        Message removeMessage = new UserListItemMessage(getPlayerListName(), false, (short)0);
        playerListName = name;
        Message reAddMessage = new UserListItemMessage(getPlayerListName(), true, (short)0);
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(removeMessage);
            ((GlowPlayer) player).getSession().send(reAddMessage);
        }
    }

    public Location getCompassTarget() {
        return compassTarget;
    }

    public void setCompassTarget(Location loc) {
        compassTarget = loc;
        session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneak) {
        if (EventFactory.onPlayerToggleSneak(this, sneak).isCancelled()) {
            return;
        }
        this.sneaking = sneak;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 0, new Byte((byte) (this.sneaking ? 0x02: 0))));
        // FIXME: other bits in the bitmask would be wiped out
        EntityMetadataMessage message = new EntityMetadataMessage(id, metadata);
        for (Player player : world.getPlayers()) {
            if (player != this && canSee((GlowPlayer) player)) {
                ((GlowPlayer) player).session.send(message);
            }
        }
    }

    public boolean isSprinting() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSprinting(boolean sprinting) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSleepingIgnored() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSleepingIgnored(boolean isSleeping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setGameMode(GameMode mode) {
        boolean changed = getGameMode() != mode;
        super.setGameMode(mode);
        if (changed) session.send(new StateChangeMessage((byte) 3, (byte) mode.getValue()));
    }

    public int getExperience() {
        return experience % ((getLevel() + 1) * 10);
    }

    public void setExperience(int exp) {
        setTotalExperience(experience - getExperience() + exp);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        int calcExperience = getExperience();
        this.level = level;
        for (int i = 0; i <= level; i++) {
            calcExperience += (level + 1) * 10;
        }
        setExperience(calcExperience);
        session.send(createExperienceMessage());
    }

    public int getTotalExperience() {
        return experience;
    }

    public void setTotalExperience(int exp) {
        int calcExperience = exp;
        this.experience = exp;
        level = 0;
        while ((calcExperience -= (getLevel() + 1) * 10) > 0) ++level;
        session.send(createExperienceMessage());
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(float value) {
        exhaustion = value;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float value) {
        saturation = value;
        session.send(createHealthMessage());
    }
    
    // -- Actions

    /**
     * Teleport the player.
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        if (this.location != null && this.location.getWorld() != null) {
            PlayerTeleportEvent event = EventFactory.onPlayerTeleport(this, getLocation(), location);
            if (event.isCancelled()) return false;
            location = event.getTo();
        }
        if (location.getWorld() != world) {
            GlowWorld oldWorld = world;
            world.getEntityManager().deallocate(this);
            
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);
            
            for (GlowChunk.Key key : knownChunks) {
                session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            }
            knownChunks.clear();
            
            session.send(new RespawnMessage((byte) world.getEnvironment().getId(), (byte)1, (byte) getGameMode().getValue(), (short) world.getMaxHeight(), world.getSeed()));
            streamBlocks(); // stream blocks
            
            setCompassTarget(world.getSpawnLocation()); // set our compass target
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location; // take us to spawn position
            session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2), (byte)0)); // send the world's weather
            reset();
            EventFactory.onPlayerChangedWorld(this, oldWorld);
        } else {
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location;
            reset();
        }
        
        return true;
    }

    public void sendMessage(String message) {
        for (String line : TextWrapper.wrapText(message)) {
            sendRawMessage(line);
        }
    }

    public void sendRawMessage(String message) {
        session.send(new ChatMessage(message.length() <= 119 ? message : message.substring(0, 119)));
    }

    public void kickPlayer(String message) {
        session.disconnect(message == null ? "" : message);
    }

    public boolean performCommand(String command) {
        return getServer().dispatchCommand(this, command);
    }

    /**
     * Says a message (or runs a command).
     *
     * @param text message to print
     */
    public void chat(String text) {
        if (text.startsWith("/")) {
            try {
                if (EventFactory.onPlayerCommand(this, text).isCancelled()) {
                    return;
                }
                
                if (!performCommand(text.substring(1))) {
                    String firstword = text.substring(1);
                    if (firstword.indexOf(' ') >= 0) {
                        firstword = firstword.substring(0, firstword.indexOf(' '));
                    }
                    
                    sendMessage(ChatColor.GRAY + "Command not found: " + firstword);
                }
            }
            catch (Exception ex) {
                sendMessage(ChatColor.RED + "An internal error occured while executing your command.");
                getServer().getLogger().log(Level.SEVERE, "Exception while executing command: {0}", ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            PlayerChatEvent event = EventFactory.onPlayerChat(this, text);
            if (event.isCancelled()) {
                return;
            }
            
            String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
            getServer().getLogger().info(message);
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(message);
            }
        }
    }

    public void saveData() {
        saveData(true);
    }

    public void saveData(boolean async) {
        final GlowWorld dataWorld = (GlowWorld) server.getWorlds().get(0);
        if (async) {
            final GlowPlayer player = this;
            server.getStorageQueue().queue(new StorageOperation() {
                @Override
                public boolean isParallel() {
                    return true;
                }

                @Override
                public String getGroup() {
                    return getName() + "_" + getWorld().getName();
                }

                @Override
                public boolean queueMultiple() {
                    return true;
                }

                @Override
                public String getOperation() {
                    return "player-data-save";
                }

                public void run() {
                    dataWorld.getMetadataService().writePlayerData(player);
                }
            });
        } else {
            dataWorld.getMetadataService().writePlayerData(this);
        }
    }

    public void loadData() {
        
        GlowWorld dataWorld = (GlowWorld)server.getWorlds().get(0);
        dataWorld.getMetadataService().readPlayerData(this);
    }
    
    // -- Data transmission
    
    public void playNote(Location loc, Instrument instrument, Note note) {
        playNote(loc, instrument.getType(), note.getId());
    }

    public void playNote(Location loc, byte instrument, byte note) {
        session.send(new PlayNoteMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note));
    }

    public void playEffect(Location loc, Effect effect, int data) {
        session.send(new PlayEffectMessage(effect.getId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data));
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    public void sendBlockChange(Location loc, int material, byte data) {
        session.send(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
    }

    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // -- Achievements & Statistics [mostly borrowed from CraftBukkit]

    public void awardAchievement(Achievement achievement) {
        sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        sendStatistic(statistic.getId(), amount);
    }

    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        if (!statistic.isSubstatistic()) {
            throw new IllegalArgumentException("Given statistic is not a substatistic");
        }
        if (statistic.isBlock() != material.isBlock()) {
            throw new IllegalArgumentException("Given material is not valid for this substatistic");
        }

        int mat = material.getId();

        if (!material.isBlock()) {
            mat -= 255;
        }

        sendStatistic(statistic.getId() + mat, amount);
    }

    private void sendStatistic(int id, int amount) {
        while (amount > Byte.MAX_VALUE) {
            sendStatistic(id, Byte.MAX_VALUE);
            amount -= Byte.MAX_VALUE;
        }

        if (amount > 0) {
            session.send(new StatisticMessage(id, (byte) amount));
        }
    }
    
    // -- Inventory

    public void updateInventory() {
        getInventory().setContents(getInventory().getContents());
    }
    
    /**
     * Get the current item on the player's cursor, for inventory screen purposes.
     * @return The ItemStack the player is holding.
     */
    public ItemStack getItemOnCursor() {
        return itemOnCursor;
    }
    
    /**
     * Set the item on the player's cursor, for inventory screen purposes.
     * @param item The ItemStack to set the cursor to.
     */
    public void setItemOnCursor(ItemStack item) {
        itemOnCursor = item;
        if (item == null) {
            session.send(new SetWindowSlotMessage(-1, -1));
        } else {
            session.send(new SetWindowSlotMessage(-1, -1, item.getTypeId(), item.getAmount(), item.getDurability()));
        }
    }
    
    /**
     * Inform the client that an item has changed.
     * @param inventory The GlowInventory in which a slot has changed.
     * @param slot The slot number which has changed.
     * @param item The ItemStack which the slot has changed to.
     */
    public void onSlotSet(GlowInventory inventory, int slot, ItemStack item) {
        if (inventory == getInventory()) {
            int type = item == null ? -1 : item.getTypeId();
            int data = item == null ? 0 : item.getDurability();
            
            int equipSlot = -1;
            if (slot == getInventory().getHeldItemSlot()) {
                equipSlot = EntityEquipmentMessage.HELD_ITEM;
            } else if (slot == GlowPlayerInventory.HELMET_SLOT) {
                equipSlot = EntityEquipmentMessage.HELMET_SLOT;
            } else if (slot == GlowPlayerInventory.CHESTPLATE_SLOT) {
                equipSlot = EntityEquipmentMessage.CHESTPLATE_SLOT;
            } else if (slot == GlowPlayerInventory.LEGGINGS_SLOT) {
                equipSlot = EntityEquipmentMessage.LEGGINGS_SLOT;
            } else if (slot == GlowPlayerInventory.BOOTS_SLOT) {
                equipSlot = EntityEquipmentMessage.BOOTS_SLOT;
            }
            
            if (equipSlot >= 0) {
                EntityEquipmentMessage message = new EntityEquipmentMessage(getEntityId(), equipSlot, type, data);
                for (GlowPlayer player : new ArrayList<GlowPlayer>(getWorld().getRawPlayers())) {
                    if (player != this && player.canSee(this)) {
                        player.getSession().send(message);
                    }
                }
            }
        }
        
        if (item == null) {
            session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot)));
        } else {
            session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot), item.getTypeId(), item.getAmount(), item.getDurability()));
        }
    }
    
    // -- Goofy relative time stuff --
    
    /**
     * Sets the current time on the player's client. When relative is true the player's time
     * will be kept synchronized to its world time with the specified offset.
     *
     * When using non relative time the player's time will stay fixed at the specified time parameter. It's up to
     * the caller to continue updating the player's time. To restore player time to normal use resetPlayerTime().
     *
     * @param time The current player's perceived time or the player's time offset from the server time.
     * @param relative When true the player time is kept relative to its world time.
     */
    public void setPlayerTime(long time, boolean relative) {
        timeOffset = time % 24000;
        timeRelative = relative;
        
        if (timeOffset < 0) timeOffset += 24000;
    }

    /**
     * Returns the player's current timestamp.
     *
     * @return
     */
    public long getPlayerTime() {
        if (timeRelative) {
            // add timeOffset ticks to current time
            return (world.getTime() + timeOffset) % 24000;
        } else {
            // return time offset
            return timeOffset % 24000;
        }
    }

    /**
     * Returns the player's current time offset relative to server time, or the current player's fixed time
     * if the player's time is absolute.
     *
     * @return
     */
    public long getPlayerTimeOffset() {
        return timeOffset;
    }

    /**
     * Returns true if the player's time is relative to the server time, otherwise the player's time is absolute and
     * will not change its current time unless done so with setPlayerTime().
     *
     * @return true if the player's time is relative to the server time.
     */
    public boolean isPlayerTimeRelative() {
        return timeRelative;
    }

    /**
     * Restores the normal condition where the player's time is synchronized with the server time.
     * Equivalent to calling setPlayerTime(0, true).
     */
    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }
    
    // ==== Spout ====
    
    /**
     * A holder for Spoutcraft-only information.
     */
    private class SpoutcraftData {
        public boolean enabled = false, preCached = false;

        public InGameScreen inGameScreen = new InGameScreen(getEntityId());
        public Screen screen = inGameScreen;
        public ScreenType currentScreen;
        
        public RenderDistance currentRender = RenderDistance.NORMAL;
        public RenderDistance maxRender = RenderDistance.FAR;
        public RenderDistance minRender = RenderDistance.TINY;
        
        public String versionString = "not set";
        
        public Keyboard keyFront, keyBack, keyLeft, keyRight, keyJump;
        public Keyboard keyInv, keyDrop, keyChat, keyFog, keySneak;
        
        public String clipboard;
        
        public final Queue<SpoutPacket> preEnableQueue = new LinkedList<SpoutPacket>();
    }
    
    /**
     * The SpoutCraft-only information attached to this player.
     */
    private final SpoutcraftData spoutcraft = new SpoutcraftData();
    
    // basics
    
    public void enableSpoutcraft() {
        if (!spoutcraft.enabled) {
            spoutcraft.enabled = true;
            EventFactory.onSpoutCraftEnable(this);
            synchronized (spoutcraft.preEnableQueue) {
                for (SpoutPacket packet : spoutcraft.preEnableQueue) {
                    session.send(new SpoutMessage(packet));
                }
                spoutcraft.preEnableQueue.clear();
            }
            
            getMainScreen().attachWidget(null, new GenericLabel("Glowstone server" + server.getVersion()).setAnchor(WidgetAnchor.TOP_LEFT));
            
            if (isOp()) {
                sendPacket(new PacketAllowVisualCheats(true, true, true, true, true, true, true));
            }
        }
    }
    
    public void setSpoutcraftVersion(String version) {
        if (isSpoutCraftEnabled()) {
            spoutcraft.versionString = version;
        }
    }

    public boolean isSpoutCraftEnabled() {
        return spoutcraft.enabled;
    }
    
    // inventory

    public boolean closeActiveWindow() {
        if (spoutcraft.inGameScreen.getActivePopup() == null) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            return spoutcraft.inGameScreen.closePopup();
        }
    }

    public boolean openInventoryWindow(Inventory inventory) {
        return openInventoryWindow(inventory, null, false);
    }

    public boolean openInventoryWindow(Inventory inventory, Location location) {
        return openInventoryWindow(inventory, location, false);
    }

    public boolean openInventoryWindow(Inventory inventory, Location location, boolean ignoreDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean openWorkbenchWindow(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Location getActiveInventoryLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setActiveInventoryLocation(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // gui

    public InGameScreen getMainScreen() {
        return spoutcraft.inGameScreen;
    }

    public Screen getCurrentScreen() {
        Screen screen = spoutcraft.screen;
        if (screen == null) screen = spoutcraft.inGameScreen;
        return screen;
    }

    // keys

    public Keyboard getForwardKey() {
        return spoutcraft.keyFront;
    }

    public Keyboard getBackwardKey() {
        return spoutcraft.keyBack;
    }

    public Keyboard getLeftKey() {
        return spoutcraft.keyLeft;
    }

    public Keyboard getRightKey() {
        return spoutcraft.keyRight;
    }

    public Keyboard getJumpKey() {
        return spoutcraft.keyJump;
    }

    public Keyboard getInventoryKey() {
        return spoutcraft.keyInv;
    }

    public Keyboard getDropItemKey() {
        return spoutcraft.keyDrop;
    }

    public Keyboard getChatKey() {
        return spoutcraft.keyChat;
    }

    public Keyboard getToggleFogKey() {
        return spoutcraft.keyFog;
    }

    public Keyboard getSneakKey() {
        return spoutcraft.keySneak;
    }

	public void updateKeys(byte[] keys) {
		spoutcraft.keyFront = Keyboard.getKey(keys[0]);
		spoutcraft.keyBack = Keyboard.getKey(keys[2]);
		spoutcraft.keyLeft = Keyboard.getKey(keys[1]);
		spoutcraft.keyRight = Keyboard.getKey(keys[3]);
		spoutcraft.keyJump = Keyboard.getKey(keys[4]);
		spoutcraft.keyInv = Keyboard.getKey(keys[5]);
		spoutcraft.keyDrop = Keyboard.getKey(keys[6]);
		spoutcraft.keyChat = Keyboard.getKey(keys[7]);
		spoutcraft.keyFog = Keyboard.getKey(keys[8]);
		spoutcraft.keySneak = Keyboard.getKey(keys[9]);
	}
    
    // render distance

    public RenderDistance getRenderDistance() {
        return spoutcraft.currentRender;
    }

    public void setRenderDistance(RenderDistance distance) {
        setRenderDistance(distance, true);
    }

    public void setRenderDistance(RenderDistance currentRender, boolean update) {
        if (spoutcraft.enabled) {
            spoutcraft.currentRender = currentRender;
            if (update) {
                sendPacket(new PacketRenderDistance(currentRender, null, null));
            }
        }
    }

    public boolean sendInventoryEvent() {
        return true; // TODO
    }

    public PlayerInformation getInformation() {
        return information;
    }

    public RenderDistance getMaximumRenderDistance() {
        return spoutcraft.maxRender;
    }

    public void setMaximumRenderDistance(RenderDistance maximum) {
        if (spoutcraft.enabled) {
            spoutcraft.maxRender = maximum;
            sendPacket(new PacketRenderDistance(null, maximum, null));
        }
    }

    public void resetMaximumRenderDistance() {
        setMaximumRenderDistance(RenderDistance.FAR);
    }

    public RenderDistance getMinimumRenderDistance() {
        return spoutcraft.minRender;
    }

    public void resetMinimumRenderDistance() {
        setMinimumRenderDistance(RenderDistance.TINY);
    }

    public void setMinimumRenderDistance(RenderDistance minimum) {
        if (spoutcraft.enabled) {
            spoutcraft.minRender = minimum;
            sendPacket(new PacketRenderDistance(null, minimum, null));
        }
    }
    
    // clipboard

    public String getClipboardText() {
        return spoutcraft.clipboard;
    }

    public void setClipboardText(String text) {
        setClipboardText(text, true);
    }

    public void setClipboardText(String text, boolean update) {
        if (spoutcraft.enabled) {
            spoutcraft.clipboard = text;
            if (update) {
                sendPacket(new PacketClipboardText(text));
            }
        }
    }
    
    // senders & helpers

    public void sendNotification(String title, String message, Material toRender) {
        sendPacket(new PacketAlert(title, message, toRender.getId()));
    }

    public void sendNotification(String title, String message, Material toRender, short data, int time) {
        sendPacket(new PacketNotification(title, message, toRender.getId(), data, time));
    }

    private byte[] urlBuffer = new byte[16384];
    
    public void setTexturePack(final String url) {
        if (url.length() < 5 || !url.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("Invalid URL! Texture pack urls must be in .zip format!");
        }
        new CRCStore.URLCheck(url, urlBuffer, new CRCStoreRunnable() {
            private long crc;
            public void setCRC(Long crc) {
                this.crc = crc;
            }

            public void run() {
                sendPacket(new PacketTexturePack(url, crc));
            }
        }).start();
    }

    public void resetTexturePack() {
        sendPacket(new PacketTexturePack("[none]", 0));
    }

    public double getGravityMultiplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setGravityMultiplier(double multiplier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSwimmingMultiplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSwimmingMultiplier(double multiplier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getWalkingMultiplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setWalkingMultiplier(double multiplier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getJumpingMultiplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setJumpingMultiplier(double multiplier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getAirSpeedMultiplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAirSpeedMultiplier(double multiplier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetMovement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canFly() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCanFly(boolean fly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Location getLastClickedLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendPacket(SpoutPacket packet) {
        if (spoutcraft.enabled) {
            session.send(new SpoutMessage(packet));
        } else {
            spoutcraft.preEnableQueue.add(packet);
        }
    }

    public void sendPacket(MCPacket packet) {
        Message message = makeMessage(packet);
        if (message != null) {
            session.send(message);
        }
    }

    public boolean isPreCachingComplete() {
        return spoutcraft.preCached;
    }

    public void setPreCachingComplete(boolean complete) {
        spoutcraft.preCached = complete;
    }

    public void sendImmediatePacket(MCPacket packet) {
        sendPacket(packet); // TODO
    }

    public void reconnect(String hostname, int port) {
        if (hostname.contains(":")) {
            throw new IllegalArgumentException("Hostname must not contain ':'!");
        }
        kickPlayer("[Redirect] Please reconnect to : " + hostname + ":" + port);
    }

    public void reconnect(String hostname) {
        if (hostname.contains(":")) {
            String[] split = hostname.split(":");
            if (split.length != 2) {
                throw new IllegalArgumentException("Hostname must not contain more than one ':'!");
            }
            int port;
            try {
            	port = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Number expected, String found!");
            }
            reconnect(split[0], port);
        } else {
            kickPlayer("[Redirect] Please reconnect to : " + hostname);
        }
    }

    public ScreenType getActiveScreen() {
        return spoutcraft.currentScreen;
    }

    public void openSignEditGUI(Sign sign) {
        sendPacket(new PacketOpenSignGUI(sign.getX(), sign.getY(), sign.getZ()));
    }

    public void openScreen(ScreenType type) {
        openScreen(type, true);
    }

    public void openScreen(ScreenType type, boolean packet) {
        if (isSpoutCraftEnabled()) {
            if (packet) sendPacket(new PacketOpenScreen(type));
            spoutcraft.currentScreen = type;
        }
    }

    private Message makeMessage(MCPacket packet) {
        if (packet instanceof MCPacket0KeepAlive) {
            // Added field here in 1.8, Spout isn't updated.
            return null;
        } else if (packet instanceof MCPacket3Chat) {
            MCPacket3Chat chat = (MCPacket3Chat) packet;
            return new ChatMessage(chat.getMessage());
        } else if (packet instanceof MCPacket17) {
            MCPacket17 bed = (MCPacket17) packet;

            // Currently no corresponding Message
            return new BedMessage(bed.getEntityId(), bed.getBed() == 1, bed.getBlockX(), bed.getBlockY(), bed.getBlockZ());
        } else if (packet instanceof MCPacket18ArmAnimation) {
            MCPacket18ArmAnimation anim = (MCPacket18ArmAnimation) packet;
            return new AnimateEntityMessage(anim.getEntityId(), anim.getAnimate());
        } else if (packet instanceof MCPacket51MapChunkUncompressed) {
            MCPacket51MapChunkUncompressed chunk = (MCPacket51MapChunkUncompressed) packet;
            return new CompressedChunkMessage(chunk.getX(), chunk.getY(), chunk.getZ(), chunk.getSizeX(), chunk.getSizeX(), chunk.getSizeZ(), chunk.getUncompressedChunkData());
        } else if (packet instanceof MCPacket51MapChunk) {
            // Currently no corresponding Message
            return null;
        } else {
            // Unhandleable MCPacketUnknown or an otherwise unknown packet type
            return null;
        }
    }

    /**
     * Render a map and send it to the player in its entirety. This may be used
     * when streaming the map in the normal manner is not desirbale.
     * 
     * @param map The map to be sent
     */
    public void sendMap(MapView map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        session.send(createHealthMessage());
    }

    public int getMaxHealth() {
        return 20;
    }

    @Override
    public void addEntityEffect(ActiveEntityEffect effect) {
        super.addEntityEffect(effect);
        EntityEffectMessage msg = new EntityEffectMessage(getEntityId(), effect.getEffect().getId(), effect.getAmplitude(), effect.getDuration());
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(msg);
        }
    }

    @Override
    public void removeEntityEffect(ActiveEntityEffect effect) {
        super.removeEntityEffect(effect);
        EntityRemoveEffectMessage msg = new EntityRemoveEffectMessage(getEntityId(), effect.getEffect().getId());
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(msg);
        }
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        return new SpawnPlayerMessage(id, dispNameAsEntityName ? displayName : getName(), x, y, z, yaw, pitch, 0);
    }

    public int getFoodLevel() {
        return food;
    }

    public void setFoodLevel(int food) {
        this.food = Math.min(food, 20);
        session.send(createHealthMessage());
    }

    public Message createHealthMessage() {
        return new HealthMessage(getHealth(), getFoodLevel(), getSaturation());
    }
    
    public Message createExperienceMessage() {
        return new ExperienceMessage((byte)getExperience(), (byte)getLevel(), (short)getTotalExperience());
    }
    
    public void addExperience(int exp) {
        experience += exp;
        level += (experience / 200);
        experience %= 200;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("name", getName());
        return ret;
    }
}

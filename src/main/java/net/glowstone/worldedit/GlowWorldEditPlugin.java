package net.glowstone.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.Pattern;
import net.glowstone.GlowServer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GlowWorldEditPlugin extends WorldEditPlugin {

    /**
     * Used to wrap a Bukkit Player as a LocalPlayer.
     *
     * @param player
     * @return
     */
    public BukkitPlayer wrapPlayer(Player player) {
        return new GlowWEPlayer(this, this.getServerInterface(), player);
    }

    void init(PluginLoader loader, Server server,
            PluginDescriptionFile description, File dataFolder, File file,
            ClassLoader classLoader) {
        initialize(loader, server, description, dataFolder, file, classLoader);
    }
}

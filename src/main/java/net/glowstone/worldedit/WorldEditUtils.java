package net.glowstone.worldedit;

import com.sk89q.worldedit.patterns.Pattern;
import net.glowstone.GlowServer;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author zml2008
 */
public class WorldEditUtils {

    public static Plugin setUpWorldEdit(GlowServer server) {
        //Get the JavaPluginLoader
        Map<Pattern, PluginLoader> pluginLoaderMap = getField(server.getPluginManager(), "fileAssociations");
        JavaPluginLoader pluginLoader = null;
        for (PluginLoader loader : pluginLoaderMap.values()) {
            if (loader instanceof JavaPluginLoader) {
                pluginLoader = (JavaPluginLoader) loader;
                break;
            }
        }
        if (pluginLoader == null) {
            throw new RuntimeException("Unable to get JavaPluginLoader");
        }
        File pluginFile;
        try {
            pluginFile = downloadWorldEdit();
        } catch (IOException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start." + e);
        }
        JarFile pluginJar;
        try {
            pluginJar = new JarFile(pluginFile);
        } catch (IOException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start" + e);
        }
        // Load the plugin description, etc
        JarEntry descEntry = pluginJar.getJarEntry("plugin.yml");
        PluginClassLoader pluginClassLoader = null;
        try {
            pluginClassLoader = new PluginClassLoader(pluginLoader, new URL[]{pluginFile.toURI().toURL()}, GlowServer.class.getClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start." + e);
        }
        PluginDescriptionFile description = null;
        try {
            description = new PluginDescriptionFile(pluginJar.getInputStream(descEntry));
        } catch (InvalidDescriptionException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start." + e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start" + e);
        }
        try {
            // Class.forName(description.getMain(), true, pluginClassLoader);
            GlowServer.class.getClassLoader().loadClass("com.sk89q.worldedit.bukkit.WorldEditPlugin");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading WorldEdit. Server cannot start" + e);
        }
        File dataFolder = new File(server.getConfigDir(), description.getName());
        dataFolder.mkdirs();
        GlowWorldEditPlugin plugin = new GlowWorldEditPlugin();
        plugin.init(pluginLoader, server, description, dataFolder, pluginFile, pluginClassLoader);
        plugin.onLoad();

        PluginManager pluginManager = server.getPluginManager();
        List<Plugin> plugins = getField(pluginManager, "plugins");
        Map<String, Plugin> lookupNames = getField(pluginManager, "lookupNames");
        plugins.add(plugin);
        lookupNames.put(description.getName(), plugin);
        pluginManager.enablePlugin(plugin);
        return plugin;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File downloadWorldEdit() throws IOException {
        File worldEditFile = new File("lib", "WorldEdit.jar");
        if (worldEditFile.exists() && worldEditFile.isFile()) {
            return worldEditFile;
        }
        worldEditFile.getParentFile().mkdirs();
        worldEditFile.createNewFile();
        FileOutputStream out = new FileOutputStream(worldEditFile);
        URL weUrl = new URL("http://build.sk89q.com/job/WorldEdit/lastSuccessfulBuild/artifact/target/worldedit-4.7-SNAPSHOT.jar");
        InputStream stream = weUrl.openConnection().getInputStream();
        try {
            if (stream != null) {
                byte[] buf = new byte[8192];
                int length = 0;
                while ((length = stream.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(GlowServer.class.getClassLoader(), worldEditFile.toURI().toURL());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return worldEditFile;
    }
}

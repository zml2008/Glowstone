package net.glowstone.spout;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.player.FileManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class GlowFileManager implements FileManager, GlowSpoutComponent {

    public List<String> getCache(Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToPreLoginCache(Plugin plugin, File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToPreLoginCache(Plugin plugin, String fileUrl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToPreLoginCache(Plugin plugin, Collection<File> files) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToPreLoginCache(Plugin plugin, List<String> fileUrls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToPreLoginCache(Plugin plugin, InputStream input, String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToCache(Plugin plugin, File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToCache(Plugin plugin, String fileUrl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToCache(Plugin plugin, Collection<File> file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToCache(Plugin plugin, List<String> fileUrls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addToCache(Plugin plugin, InputStream input, String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFromCache(Plugin plugin, String file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFromCache(Plugin plugin, List<String> file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canCache(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canCache(String fileUrl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void registerPlayer(SpoutPlayer player) {}

    public void resetAll() {
        // TODO: Implement this class
    }
}

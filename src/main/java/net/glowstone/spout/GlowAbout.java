package net.glowstone.spout;

import net.glowstone.block.BlockID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;

public class GlowAbout extends GenericPopup {
    private static final ItemStack GLOWSTONE_STACK = new ItemStack(BlockID.GLOWSTONE);

    public static void setUp() {
        SpoutManager.getKeyBindingManager().registerBinding("About Glowstone", Keyboard.KEY_U, "About Glowstone", new BindingExecutionDelegate() {
            public void keyPressed(KeyBindingEvent event) {
                if (event.getScreenType() == ScreenType.GAME_SCREEN && event.getPlayer().getMainScreen().getActivePopup() == null) {
                    event.getPlayer().getMainScreen().attachPopupScreen(new GlowAbout());
                }
            }

            public void keyReleased(KeyBindingEvent event) {

            }
        }, Bukkit.getServer().getPluginManager().getPlugins()[0]);
    }

    public GlowAbout() {
        setHeight(/* (int)(getScreen().getHeight() * .75) */ 250).setWidth(/* (getScreen().getWidth() / 2)*/ 300).setAnchor(WidgetAnchor.CENTER_CENTER);
        setBgVisible(true);
        attachWidget(null, new GenericItemWidget(GLOWSTONE_STACK).setHeight(80).setWidth(80).setAnchor(WidgetAnchor.TOP_LEFT));
    }
}

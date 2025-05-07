package me.gergerapex1.raiddrinker;

import me.gergerapex1.raiddrinker.core.ItemHandlers;
import me.gergerapex1.raiddrinker.core.RaidHandler;
import me.gergerapex1.raiddrinker.core.ToggleStatus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class RaidDrinkerClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RaidDrinker");

    ItemHandlers itemHandlers = null;
    static TickScheduler scheduler = null;

    public static boolean raidActive = false;
    public static boolean raidFinished = false;
    public static boolean toggleMod = false;

    @Override
    public void onInitializeClient() {
        scheduler = new TickScheduler();
        itemHandlers= new ItemHandlers();
        KeyBinding toggleModKeybind = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.autopotion.toggle_auto", GLFW.GLFW_KEY_U, "category.raiddrinker"));
        new ToggleStatus(toggleModKeybind).init();
        new RaidHandler().init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleMod && client.player != null) {
                if (raidActive) {
                    LOGGER.info(client.player.getHealth());
                    if(client.player.getHealth() < 8.0f || client.player.getHungerManager().getFoodLevel() < 5.0f) {
                        itemHandlers.tryEat(client);
                    } else {
                        itemHandlers.switchToHotbarSword(client.player);
                        itemHandlers.attackWithSword(client);
                    }
                    if (raidFinished) {
                        itemHandlers.tryDrinkOminousPotion(client);
                    }
                } else {
                    itemHandlers.tryDrinkOminousPotion(client);
                }
            }
        });
    }

    public static void setToggleMod(boolean status) {
        toggleMod = status;
    }

    public static boolean getToggleMod() {
        return toggleMod;
    }

    public static TickScheduler getScheduler() {
        return scheduler;
    }

    public static void clearRaidStatus() {
        raidActive = false;
        raidFinished = false;
    }

}

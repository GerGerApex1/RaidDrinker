package me.gergerapex1.raiddrinker;

import java.util.List;
import me.gergerapex1.raiddrinker.core.ItemHandlers;
import me.gergerapex1.raiddrinker.core.RaidHandler;
import me.gergerapex1.raiddrinker.core.ToggleStatus;
import me.gergerapex1.raiddrinker.core.VexHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.mob.VexEntity;
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

    public static float onTogglePitch = 0.0f;
    public static float onToggleYaw = 0.0f;

    @Override
    public void onInitializeClient() {
        scheduler = new TickScheduler();
        itemHandlers= new ItemHandlers();
        KeyBinding toggleModKeybind = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.autopotion.toggle_auto", GLFW.GLFW_KEY_U, "category.raiddrinker"));
        ToggleStatus toggleStatus = new ToggleStatus(toggleModKeybind);
        toggleStatus.init();

        new RaidHandler().init();
        VexHandler vexHandler = new VexHandler();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleMod && client.player != null) {
                List<VexEntity> entityList = vexHandler.scanVexNearby(client);
                if(!entityList.isEmpty()) {
                    for (VexEntity vex : entityList) {
                        vexHandler.lookAtEntity(client, vex);
                        itemHandlers.switchToHotbarSword(client.player);
                        itemHandlers.attackWithSword(client);
                    }
                } else if(client.player.getPitch() != onTogglePitch || client.player.getYaw() != onToggleYaw) {
                    returnToStartingCamera(client);
                }
                if (raidActive) {
                    if(client.player.getHungerManager().getFoodLevel() > 10.0f &&
                        (client.player.getHealth() < 8.0f || client.player.getHungerManager().getFoodLevel() < 8.0f)) {
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
    private void returnToStartingCamera(MinecraftClient client) {
        if (client.player != null) return;
        client.player.setYaw(onToggleYaw);
        client.player.setPitch(onTogglePitch);
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

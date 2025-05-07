package me.gergerapex1.raiddrinker.core;

import static me.gergerapex1.raiddrinker.RaidDrinkerClient.toggleMod;

import me.gergerapex1.raiddrinker.RaidDrinkerClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;

public class ToggleStatus {
    private final KeyBinding toggleModKeybind;
    public ToggleStatus(KeyBinding toggleModKeybind) {
        this.toggleModKeybind = toggleModKeybind;
    }
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (toggleModKeybind.wasPressed()) {
                RaidDrinkerClient.setToggleMod(!RaidDrinkerClient.getToggleMod());
                if (!toggleMod) {
                    RaidDrinkerClient.clearRaidStatus();
                }
            }
        });
    }
}

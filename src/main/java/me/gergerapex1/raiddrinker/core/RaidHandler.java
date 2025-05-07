package me.gergerapex1.raiddrinker.core;

import static me.gergerapex1.raiddrinker.RaidDrinkerClient.raidActive;
import static me.gergerapex1.raiddrinker.RaidDrinkerClient.raidFinished;

import java.util.Map;
import java.util.UUID;
import me.gergerapex1.raiddrinker.mixin.BossBarHudMixin;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.hud.ClientBossBar;

public class RaidHandler {
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                return;
            }
            Map<UUID, ClientBossBar> bossBarMap = ((BossBarHudMixin) client.inGameHud.getBossBarHud()).getBossBar();
            if (bossBarMap.isEmpty()) {
                raidActive = false;
                raidFinished = false;
            } else {
                for (ClientBossBar bossBar : bossBarMap.values()) {
                    if (bossBar.getName().getString().toLowerCase().contains("raid")
                        & bossBar.getName().getString().toLowerCase().contains("victory")) {
                        raidFinished = true;
                        raidActive = false;
                        return;
                    }
                    // Raid boss bar found
                    // Raid boss bar not found, but it was previously active
                    raidActive = bossBar.getName().getString().toLowerCase().contains("raid");
                }
            }
        });
    }
}

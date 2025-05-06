package me.gergerapex1.raiddrinker.client;

import me.gergerapex1.raiddrinker.TickScheduler;
import me.gergerapex1.raiddrinker.mixin.BossBarHudMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.UUID;

public class RaidDrinkerClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("RaidDrinker");
    private KeyBinding toggleModKeybind;
    private boolean raidActive = false;
    private boolean raidFinished = false;
    private UUID currentPlayerUUID = null;
    private boolean toggleMod = false;
    TickScheduler scheduler = null;
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        scheduler = new TickScheduler();
        toggleModKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autopotion.toggle_auto",
                GLFW.GLFW_KEY_U,
                "category.autopotion"
        ));
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            if (networkHandler != null && client.player != null) {
                currentPlayerUUID = client.player.getUuid();
            }
        }));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            Map<UUID, ClientBossBar> bossBarMap = ((BossBarHudMixin) client.inGameHud.getBossBarHud()).getBossBar();
            if (bossBarMap.isEmpty()) {
                raidActive = false;
            } else {
                for (ClientBossBar bossBar : bossBarMap.values()) {
                    if (bossBar.getName().getString().toLowerCase().contains("raid") & bossBar.getName().getString().toLowerCase().contains("victory")) {
                        raidFinished = true;
                        raidActive = false;
                        return;
                    }
                    if (bossBar.getName().getString().toLowerCase().contains("raid")) {
                        // Raid boss bar found
                        raidActive = true;
                    } else {
                        // Raid boss bar not found, but it was previously active
                        raidActive = false;
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (toggleModKeybind.wasPressed()) {
                toggleMod = !toggleMod;
                if(!toggleMod) {
                    raidActive = false;
                    raidFinished = false;
                }
            }
            if (toggleMod && client.player != null) {
                if (raidActive) {
                    switchToHotbarSword(client.player);
                    attackWithSword(client);
                    if (raidFinished) {
                        tryDrinkOminousPotion(client);
                    }
                } else {
                    tryDrinkOminousPotion(client);
                }
            }
        });
    }
    private void attackWithSword(MinecraftClient client) {
        tickCounter++;
        if (client.player == null) return;
        HitResult hitResult = client.crosshairTarget;
        if (tickCounter % 20 != 0) return;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            PlayerEntity player = client.player;
            client.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
        } else {
            LOGGER.warn("No valid target to attack.");
        }

    }
    private void switchToHotbarSword(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            // Extremely poor written code here
            if (stack.getItem() == Items.NETHERITE_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            } else if (stack.getItem() == Items.DIAMOND_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            } else if (stack.getItem() == Items.GOLDEN_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            } else if (stack.getItem() == Items.IRON_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            } else if (stack.getItem() == Items.STONE_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            } else if (stack.getItem() == Items.WOODEN_SWORD) {
                player.getInventory().setSelectedSlot(i);
                return;
            }
        }
    }
    private Boolean tryDrinkOminousPotion(MinecraftClient client) {
        if (client.player.hasStatusEffect(StatusEffects.BAD_OMEN) || client.player.hasStatusEffect(StatusEffects.RAID_OMEN)) {
            return true;
        }
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.getItem().getComponents().contains(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
                try {
                    client.player.getInventory().setSelectedSlot(i);
                    client.options.useKey.setPressed(true);
                    scheduler.schedule(35, () -> client.options.useKey.setPressed(false));
                    if (ifUserHasOmen(client.player)) {
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.error("Error while trying to drink ominous potion: ", e);
                }
            }
        }
        LOGGER.warn("No Ominous Potion found in inventory!");
        return false;
    }
    private boolean ifUserHasOmen(ClientPlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.BAD_OMEN) | player.hasStatusEffect(StatusEffects.RAID_OMEN);
    }
}

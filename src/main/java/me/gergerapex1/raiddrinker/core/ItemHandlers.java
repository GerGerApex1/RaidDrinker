package me.gergerapex1.raiddrinker.core;


import me.gergerapex1.raiddrinker.RaidDrinkerClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.apache.logging.log4j.Logger;

public class ItemHandlers {

    private final Logger LOGGER = RaidDrinkerClient.LOGGER;
    private int tickCounter = 0;

    public void attackWithSword(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        tickCounter++;
        if (tickCounter % 20 != 0) {
            return;
        }
        HitResult hitResult = client.crosshairTarget;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            PlayerEntity player = client.player;
            client.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
        }
    }

    public void switchToHotbarSword(ClientPlayerEntity player) {
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

    public Boolean tryEat(MinecraftClient client) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                LOGGER.info(stack.getItem().toString());
                if(stack.getItem().getComponents().contains(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
                    continue;
                }
                ConsumableComponent consumableComponent = stack.getItem().getComponents()
                    .get(DataComponentTypes.CONSUMABLE);
                LOGGER.info(stack.getItem().toString());
                try {
                    client.player.getInventory().setSelectedSlot(i);
                    client.options.useKey.setPressed(true);
                    RaidDrinkerClient.getScheduler().schedule(consumableComponent.getConsumeTicks() + 2,
                        () -> client.options.useKey.setPressed(false));
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Error while trying to drink ominous potion: ", e);
                }
            }
        } LOGGER.warn("No Food found in inventory!");
        return false;
    }

    public Boolean tryDrinkOminousPotion(MinecraftClient client) {
        if (client.player.hasStatusEffect(StatusEffects.BAD_OMEN) || client.player.hasStatusEffect(
            StatusEffects.RAID_OMEN)) {
            return true;
        }
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.getItem().getComponents().contains(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
                ConsumableComponent consumableComponent = stack.getItem().getComponents()
                    .get(DataComponentTypes.CONSUMABLE);
                try {
                    client.player.getInventory().setSelectedSlot(i);
                    client.options.useKey.setPressed(true);
                    RaidDrinkerClient.getScheduler().schedule(consumableComponent.getConsumeTicks() + 2,
                        () -> client.options.useKey.setPressed(false));
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

    public boolean ifUserHasOmen(ClientPlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.BAD_OMEN) | player.hasStatusEffect(StatusEffects.RAID_OMEN);
    }
}

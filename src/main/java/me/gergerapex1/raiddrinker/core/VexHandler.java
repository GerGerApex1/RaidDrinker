package me.gergerapex1.raiddrinker.core;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class VexHandler {
    public static List<VexEntity> scanVexNearby(MinecraftClient client) {
        Box box = client.player.getBoundingBox().expand(2.5);
        return client.player.getWorld().getEntitiesByType(TypeFilter.instanceOf(VexEntity.class), box, entity -> true);
    }
    public void lookAtEntity(MinecraftClient client, Entity target) {
        if (client.player == null) return;

        Vec3d playerEyePos = client.player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getStandingEyeHeight() / 2.0, 0);

        double distance = playerEyePos.distanceTo(targetPos);
        BlockHitResult blockHit = client.player.getWorld().raycast(new RaycastContext(
            playerEyePos,
            targetPos,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            client.player
        ));

        if (blockHit.getType() != HitResult.Type.MISS &&
            blockHit.getPos().distanceTo(playerEyePos) < distance) {
            return;
        }

        Vec3d direction = targetPos.subtract(playerEyePos).normalize();

        double dx = direction.x;
        double dy = direction.y;
        double dz = direction.z;

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) -(Math.toDegrees(Math.atan2(dy, distanceXZ)));
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);

        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
    }
}

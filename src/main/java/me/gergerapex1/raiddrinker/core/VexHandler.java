package me.gergerapex1.raiddrinker.core;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class VexHandler {

    public ClientPlayerEntity player;

    public VexHandler(MinecraftClient client) {
        this.player = client.player;
    }
    public List<VexEntity> scanVexNearby() {
        Box box = player.getBoundingBox().expand(2.5);
        return player.getWorld().getEntitiesByType(TypeFilter.instanceOf(VexEntity.class), box, entity -> true);
    }
    public void lookAtEntity(Entity target) {
        if (player == null) return;

        Vec3d playerEyePos = player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getStandingEyeHeight() / 2.0, 0);

        double distance = playerEyePos.distanceTo(targetPos);
        BlockHitResult blockHit = player.getWorld().raycast(new RaycastContext(
            playerEyePos,
            targetPos,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            player
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

        player.setYaw(yaw);
        player.setPitch(pitch);

    }
}

package fr.villageois;

import fr.villageois.mc.Mc;
import fr.villageois.mc.Villagers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Locale;

/** Small, player-local markers; no entities, sounds or persistent labels. */
public final class InteractionHints {
    private InteractionHints() {}

    public static void tick(ServerPlayer p) {
        if (!Village.pd(p).interactionHints || p.isSpectator() || Talk.session(p) != null) return;
        var level = Village.level(p);
        Villagers.around(level, p, 8).stream()
                .filter(v -> p.distanceToSqr(v) <= 64 && !Villagers.busyWithQuiz(v) && p.hasLineOfSight(v))
                .sorted(Comparator.comparingDouble(p::distanceToSqr)).limit(3)
                .forEach(v -> particle(p, "minecraft:happy_villager", v.getX(), v.getY() + v.getBbHeight() + 0.35, v.getZ()));

        // A small local scan, once every two seconds, without loading chunks.
        BlockPos origin = p.blockPosition();
        int shown = 0;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -3, -6), origin.offset(6, 3, 6))) {
            if (!level.hasChunkAt(pos) || pos.distSqr(origin) > 36) continue;
            var block = level.getBlockState(pos);
            boolean lectern = block.is(Blocks.LECTERN);
            if (!lectern && !block.is(Blocks.BELL)) continue;
            BlockHitResult hit = level.clip(new ClipContext(p.getEyePosition(), Vec3.atCenterOf(pos),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
            if (hit.getType() != HitResult.Type.MISS && !hit.getBlockPos().equals(pos)) continue;
            particle(p, lectern ? "minecraft:enchant" : "minecraft:end_rod",
                    pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5);
            if (++shown == 4) break;
        }
    }

    private static void particle(ServerPlayer p, String type, double x, double y, double z) {
        Mc.run(String.format(Locale.ROOT,
                "execute in %s run particle %s %.3f %.3f %.3f 0.12 0.08 0.12 0 2 normal %s",
                Village.level(p).dimension().location(), type, x, y, z, Mc.sel(p)));
    }
}

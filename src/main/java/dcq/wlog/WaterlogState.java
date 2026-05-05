package dcq.wlog;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;
import java.util.WeakHashMap;

public final class WaterlogState {
    private static final Map<Level, LongSet> WATERLOGGED = new WeakHashMap<>();
    private static boolean clientEnabled;

    private WaterlogState() {
    }

    public static boolean isWaterlogged(Level level, BlockPos pos) {
        if (level.isClientSide() && !clientEnabled) {
            return false;
        }

        LongSet positions = positions(level);
        return positions != null && positions.contains(pos.asLong());
    }

    public static void setWaterlogged(Level level, BlockPos pos, boolean waterlogged) {
        if (level.isClientSide() && !clientEnabled) {
            return;
        }

        setWaterlogged(level, pos, waterlogged, true);
    }

    public static void setWaterlogged(Level level, BlockPos pos, boolean waterlogged, boolean sync) {
        if (level.isClientSide() && !clientEnabled) {
            return;
        }

        LongSet positions = positions(level);
        long packedPos = pos.asLong();

        if (waterlogged) {
            positions.add(packedPos);
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        } else {
            positions.remove(packedPos);

            if (positions.isEmpty() && !(level instanceof ServerLevel)) {
                WATERLOGGED.remove(level);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getDataStorage().computeIfAbsent(WaterlogSavedData.TYPE).setDirty();
        }

        if (sync && level instanceof ServerLevel serverLevel) {
            WaterlogSyncPayload payload = new WaterlogSyncPayload(pos.immutable(), waterlogged);

            for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
                if (ServerPlayNetworking.canSend(player, WaterlogSyncPayload.TYPE)) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        }
    }

    public static void clearIfReplaced(Level level, BlockPos pos, BlockState newState) {
        if (newState.getFluidState().isEmpty() && newState.isAir()) {
            setWaterlogged(level, pos, false);
        }
    }

    public static void setClientEnabled(boolean enabled) {
        clientEnabled = enabled;
    }

    private static LongSet positions(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(WaterlogSavedData.TYPE).positions();
        }

        return WATERLOGGED.computeIfAbsent(level, ignored -> new LongOpenHashSet());
    }
}

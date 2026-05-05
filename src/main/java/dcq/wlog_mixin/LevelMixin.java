package dcq.wlog_mixin;

import dcq.wlog.WaterlogState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void getHiddenWater(BlockPos pos, CallbackInfoReturnable<FluidState> info) {
        Level level = (Level) (Object) this;

        if (WaterlogState.isWaterlogged(level, pos)) {
            info.setReturnValue(Fluids.WATER.getSource(false));
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void clearHiddenWater(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            WaterlogState.clearIfReplaced((Level) (Object) this, pos, state);
        }
    }
}

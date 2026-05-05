package dcq.wlog_mixin;

import dcq.wlog.WaterlogState;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Unique
    private boolean waterlog$placingIntoWater;

    @Unique
    private BlockPos waterlog$placementPos;

    @Inject(method = "place", at = @At("HEAD"))
    private void captureWater(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> info) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        waterlog$placementPos = pos.immutable();
        waterlog$placingIntoWater = level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void storeWater(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> info) {
        if (waterlog$placementPos != null && info.getReturnValue().consumesAction()) {
            WaterlogState.setWaterlogged(context.getLevel(), waterlog$placementPos, waterlog$placingIntoWater);
        }

        waterlog$placementPos = null;
        waterlog$placingIntoWater = false;
    }
}

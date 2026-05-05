package dcq.wlog_mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dcq.wlog.BlockIn;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockM extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected FenceGateBlockM(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(WoodType woodType, Properties properties, CallbackInfo info) {
        ((BlockIn)(Block)(Object)this).regDefaultState(((BlockIn)(Block)(Object)this).getBlockState().setValue(WATERLOGGED, false));
    }

    @Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
    private void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "getStateForPlacement")
    public BlockState getStateForPlacement(BlockState state, BlockPlaceContext blockPlaceContext) {
        if (state != null) {
            if (state.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
                return state.setValue(WATERLOGGED,
                        blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos()).getType() == Fluids.WATER);
            }
            return state;
        }
        return null;
    }
}

package dcq.wlog_mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dcq.wlog.BlockIn;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallBannerBlock.class)
public abstract class WallBannerM extends AbstractBannerBlock implements SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected WallBannerM(DyeColor dyeColor, Properties properties) {
        super(dyeColor, properties);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(DyeColor dyeColor, BlockBehaviour.Properties properties, CallbackInfo info) {
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

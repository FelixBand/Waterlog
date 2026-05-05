package dcq.wlog_mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dcq.wlog.BlockIn;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DoorBlock.class)
public abstract class DoorBlockM extends Block implements SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected DoorBlockM(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(BlockSetType blockSetType, Properties properties, CallbackInfo info) {
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

    @Redirect(method = "setPlacedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setUpperHalf(Level level, BlockPos pos, BlockState state, int flags) {
        return level.setBlock(pos, state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER), flags);
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "updateShape")
    private BlockState updateShape(BlockState state, BlockState blockState, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
        return state.hasProperty(WATERLOGGED) ? state.setValue(WATERLOGGED, levelReader.getFluidState(blockPos).getType() == Fluids.WATER) : state;
    }
}

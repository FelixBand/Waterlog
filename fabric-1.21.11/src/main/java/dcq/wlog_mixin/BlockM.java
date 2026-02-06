package dcq.wlog_mixin;

import dcq.wlog.BlockIn;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public class BlockM implements BlockIn {

    @Shadow private BlockState defaultBlockState;
    @Shadow protected final void registerDefaultState(BlockState blockState) {}

    @Override
    public BlockState getBlockState() {
        return defaultBlockState;
    }

    @Override
    public void regDefaultState(BlockState blockState) {
        registerDefaultState(blockState);
    }
}

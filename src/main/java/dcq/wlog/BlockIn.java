package dcq.wlog;

import net.minecraft.world.level.block.state.BlockState;

public interface BlockIn {
    BlockState getBlockState();
    void regDefaultState(BlockState blockState);
}

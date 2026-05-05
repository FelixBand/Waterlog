package dcq.wlog;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WaterlogSyncPayload(BlockPos pos, boolean waterlogged) implements CustomPacketPayload {
    public static final Type<WaterlogSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Waterlog.MOD_ID, "sync"));
    public static final StreamCodec<FriendlyByteBuf, WaterlogSyncPayload> CODEC = CustomPacketPayload.codec(WaterlogSyncPayload::write, WaterlogSyncPayload::new);

    private WaterlogSyncPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(waterlogged);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dcq.wlog;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WaterlogHandshakePayload() implements CustomPacketPayload {
    public static final Type<WaterlogHandshakePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Waterlog.MOD_ID, "handshake"));
    public static final StreamCodec<FriendlyByteBuf, WaterlogHandshakePayload> CODEC = CustomPacketPayload.codec(WaterlogHandshakePayload::write, WaterlogHandshakePayload::new);

    private WaterlogHandshakePayload(FriendlyByteBuf buf) {
        this();
    }

    private void write(FriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dcq.wlog;

import dcq.wlog_mixin.ClientCommonPacketListenerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.chat.Component;

public class WaterlogClient implements ClientModInitializer {
    private static final Component MISSING_SERVER_MOD = Component.literal(
            "Waterlog changes vanilla block states and must be installed on the server too. Remove Waterlog from your client before joining vanilla servers."
    );

    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(WaterlogHandshakePayload.TYPE, (payload, context) -> {
        });
        ClientConfigurationConnectionEvents.START.register((listener, client) -> {
            if (!ClientConfigurationNetworking.canSend(WaterlogHandshakePayload.TYPE.id())) {
                Waterlog.LOGGER.warn("Disconnecting from server without Waterlog; client-only Waterlog causes vanilla block-state ID mismatches.");
                ((ClientCommonPacketListenerAccessor) listener).waterlog$getConnection().disconnect(MISSING_SERVER_MOD);
            }
        });
    }
}

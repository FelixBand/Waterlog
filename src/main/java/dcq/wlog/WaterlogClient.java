package dcq.wlog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class WaterlogClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WaterlogSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (context.client().level != null) {
                        WaterlogState.setWaterlogged(context.client().level, payload.pos(), payload.waterlogged());
                    }
                })
        );
        ClientConfigurationConnectionEvents.START.register((listener, client) ->
                WaterlogState.setClientEnabled(ClientConfigurationNetworking.canSend(WaterlogHandshakePayload.TYPE.id()))
        );
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) ->
                WaterlogState.setClientEnabled(false)
        );
    }
}

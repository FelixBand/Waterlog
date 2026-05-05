package dcq.wlog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waterlog implements ModInitializer {
	public static final String MOD_ID = "waterlog";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundConfiguration().register(WaterlogHandshakePayload.TYPE, WaterlogHandshakePayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(WaterlogSyncPayload.TYPE, WaterlogSyncPayload.CODEC);
		ServerConfigurationNetworking.registerGlobalReceiver(WaterlogHandshakePayload.TYPE, (payload, context) -> {
		});

		LOGGER.info("Waterlog initialized without changing vanilla block-state registries.");
	}
}

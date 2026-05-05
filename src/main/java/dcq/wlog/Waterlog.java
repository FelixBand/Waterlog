package dcq.wlog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waterlog implements ModInitializer {
	public static final String MOD_ID = "waterlog";
	private static final Component MISSING_CLIENT_MOD = Component.literal("Waterlog must be installed on the client too.");

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundConfiguration().register(WaterlogHandshakePayload.TYPE, WaterlogHandshakePayload.CODEC);
		PayloadTypeRegistry.clientboundConfiguration().register(WaterlogHandshakePayload.TYPE, WaterlogHandshakePayload.CODEC);
		ServerConfigurationNetworking.registerGlobalReceiver(WaterlogHandshakePayload.TYPE, (payload, context) -> {
		});
		ServerConfigurationConnectionEvents.CONFIGURE.register((listener, server) -> {
			if (!ServerConfigurationNetworking.canSend(listener, WaterlogHandshakePayload.TYPE)) {
				listener.disconnect(MISSING_CLIENT_MOD);
			}
		});

		LOGGER.info("Waterlog initialized.");
	}
}

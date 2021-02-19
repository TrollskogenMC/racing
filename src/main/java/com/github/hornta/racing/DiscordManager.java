package com.github.hornta.racing;

import com.github.hornta.racing.events.ConfigReloadedEvent;
import com.github.hornta.racing.events.RaceSessionStartEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import se.hornta.messenger.MessageManager;

import javax.security.auth.login.LoginException;
import java.util.Comparator;
import java.util.logging.Level;

public class DiscordManager implements Listener, EventListener {
	private JDA api;
	private TextChannel announceChannel;
	private boolean startAfterShutdown = false;

	DiscordManager() {
		if (RacingPlugin.getInstance().getConfiguration().<Boolean>get(ConfigKey.DISCORD_ENABLED)) {
			startup();
		}
	}

	private void startup() {
		startAfterShutdown = false;
		String token = RacingPlugin.getInstance().getConfiguration().get(ConfigKey.DISCORD_TOKEN);
		try {
			api = JDABuilder.createDefault(token).addEventListeners(this).build();
		} catch (LoginException e) {
			RacingPlugin.logger().log(Level.SEVERE, "Failed to integrate with Discord, the bot token was incorrect.");
		}
	}

	@EventHandler
	void onConfigReloaded(ConfigReloadedEvent event) {
		if (api != null) {
			api.shutdown();
		}
		if (RacingPlugin.getInstance().getConfiguration().<Boolean>get(ConfigKey.DISCORD_ENABLED)) {
			startAfterShutdown = true;
		}
	}

	@EventHandler
	void onRaceSessionStart(RaceSessionStartEvent event) {
		if (!RacingPlugin.getInstance().getConfiguration().<Boolean>get(ConfigKey.DISCORD_ENABLED)) {
			return;
		}
		MessageKey key;
		if (event.getRaceSession().getRace().getEntryFee() > 0) {
			key = MessageKey.PARTICIPATE_DISCORD_FEE;
		} else {
			key = MessageKey.PARTICIPATE_DISCORD;
		}
		int prepareTime = RacingPlugin.getInstance().getConfiguration().get(ConfigKey.RACE_PREPARE_TIME);
		Util.setTimeUnitValues();
		MessageManager.setValue("race_name", event.getRaceSession().getRace().getName());
		MessageManager.setValue("time_left", Util.getTimeLeft(prepareTime * 1000));
		MessageManager.setValue("laps", event.getRaceSession().getLaps());
		Economy economy = RacingPlugin.getInstance().getVaultEconomy();
		if (economy != null) {
			MessageManager.setValue("entry_fee", economy.format(event.getRaceSession().getRace().getEntryFee()));
		}
		announceChannel.sendMessage(MessageManager.getMessage(key)).queue();
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof ShutdownEvent) {
			if (startAfterShutdown) {
				startup();
			}
		} else if (event instanceof ReadyEvent) {
			RacingPlugin.logger().log(Level.INFO, "Successful integration with Discord.");
			String channelId = RacingPlugin.getInstance().getConfiguration().get(ConfigKey.DISCORD_ANNOUNCE_CHANNEL);
			if (channelId.isEmpty()) {
				announceChannel = api.getTextChannels().stream().min(Comparator.comparingInt(TextChannel::getPosition)).orElse(null);
			} else {
				announceChannel = api.getTextChannelById(channelId);
			}
			if (announceChannel == null) {
				RacingPlugin.logger().log(Level.SEVERE, "Couldn't find Discord channel with id: " + channelId);
			} else {
				RacingPlugin.logger().log(Level.INFO, "Found Discord channel " + announceChannel.getName() + " with id: " + announceChannel.getId());
			}
		}
	}
}

package com.github.hornta.racing.commands;

import com.github.hornta.racing.ConfigKey;
import com.github.hornta.racing.MessageKey;
import com.github.hornta.racing.RacingPlugin;
import com.github.hornta.racing.api.ParseRaceException;
import com.github.hornta.racing.events.ConfigReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import se.hornta.messenger.MessengerException;
import se.hornta.messenger.Translation;
import se.hornta.versioned_config.ConfigurationException;

public class CommandReload implements ICommandHandler {
	@Override
	public void handle(CommandSender commandSender, String[] args, int typedArgs) {
		try {
			RacingPlugin.getInstance().getConfiguration().reload();
		} catch (ConfigurationException e) {
			MessageManager.setValue("reason", e.getMessage());
			MessageManager.sendMessage(commandSender, MessageKey.RELOAD_FAILED);
			return;
		}
		Translation translation;
		try {
			RacingPlugin.getInstance().getTranslations().saveDefaults();
			translation = RacingPlugin.getInstance().getTranslations().createTranslation(RacingPlugin.getInstance().getConfiguration().get(ConfigKey.LANGUAGE));
		} catch (MessengerException e) {
			MessageManager.setValue("reason", e.getMessage());
			MessageManager.sendMessage(commandSender, MessageKey.RELOAD_MESSAGES_FAILED);
			return;
		}
		MessageManager.getInstance().setTranslation(translation);
		if (!RacingPlugin.getInstance().getRacingManager().getRaceSessions().isEmpty()) {
			MessageManager.sendMessage(commandSender, MessageKey.RELOAD_NOT_RACES);
		} else {
			try {
				RacingPlugin.getInstance().getRacingManager().load();
			} catch (ParseRaceException e) {
				MessageManager.setValue("error", e.getMessage());
				MessageManager.sendMessage(commandSender, MessageKey.RELOAD_RACES_FAILED);
				return;
			}
		}
		Bukkit.getPluginManager().callEvent(new ConfigReloadedEvent());
		MessageManager.sendMessage(commandSender, MessageKey.RELOAD_SUCCESS);
	}
}

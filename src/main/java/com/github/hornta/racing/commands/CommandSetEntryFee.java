package com.github.hornta.racing.commands;

import com.github.hornta.racing.MessageKey;
import com.github.hornta.racing.RacingManager;
import com.github.hornta.racing.RacingPlugin;
import com.github.hornta.racing.enums.RaceState;
import com.github.hornta.racing.objects.Race;
import org.bukkit.command.CommandSender;
import se.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;

public class CommandSetEntryFee extends RacingCommand implements ICommandHandler {
	public CommandSetEntryFee(RacingManager racingManager) {
		super(racingManager);
	}

	@Override
	public void handle(CommandSender commandSender, String[] args, int typedArgs) {
		Race race = racingManager.getRace(args[0]);
		if (race.getState() != RaceState.UNDER_CONSTRUCTION) {
			MessageManager.setValue("race_name", race.getName());
			MessageManager.sendMessage(commandSender, MessageKey.EDIT_NO_EDIT_MODE);
			return;
		}
		race.setEntryFee(Double.parseDouble(args[1]));
		racingManager.updateRace(race, () -> {
			MessageManager.setValue("entry_fee", RacingPlugin.getInstance().getVaultEconomy().format(race.getEntryFee()));
			MessageManager.sendMessage(commandSender, MessageKey.RACE_SET_ENTRYFEE);
		});
	}
}


package me.DevTec.ServerControlReloaded.Commands.Info;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.setting;

public class Maintenance implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
			if (Loader.has(s, "Maintenance", "Info")) {
				if (setting.lock_server) {
					Loader.config.set("Options.Maintenance.Enabled", false);
					Loader.config.save();
					setting.lock_server = false;
					Loader.sendMessages(s, "Maintenance.Disabled");
					return true;
				}

				Loader.config.set("Options.Maintenance.Enabled", true);
				Loader.config.save();
				setting.lock_server = true;
				Loader.sendMessages(s, "Maintenance.Enabled");
				return true;
			}
			Loader.noPerms(s, "Maintenance", "Info");
			return true;
	}
}
package me.DevTec.ServerControlReloaded.Commands.TpSystem;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.Loader;

public class Tpcancel implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1,
			String arg2, String[] arg3) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "TpCancel", "TpSystem")) {
			if (s instanceof Player) {
				RequestMap.cancel((Player)s);
				return true;
			}
			return true;
		}
		Loader.noPerms(s, "TpCancel", "TpSystem");
		return true;
	}

}

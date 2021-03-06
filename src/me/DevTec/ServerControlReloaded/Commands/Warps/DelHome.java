package me.DevTec.ServerControlReloaded.Commands.Warps;


import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;

public class DelHome implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (Loader.has(s, "DelHome", "Warps")) {
				if (args.length == 0) {
					Loader.Help(s, "DelHome", "Warps");
					return true;
				}
				User d = TheAPI.getUser(s.getName());
				if (d.exist("Homes." + args[0])) {
					d.setAndSave("Homes." + args[0], null);
					Loader.sendMessages(s, "Home.Delete", Placeholder.c()
							.add("%player%", p.getName())
							.add("%playername%", p.getDisplayName())
							.add("%home%", args[0]));
					return true;
				}
				Loader.sendMessages(s, "Home.NotExist", Placeholder.c()
						.add("%home%", args[0]));
				return true;
			}
			Loader.noPerms(s, "DelHome", "Warps");
			return true;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> c = new UnsortedList<>();
		if (s instanceof Player) {
			if (args.length == 1) {
				if (Loader.has(s, "DelHome", "Warps")) {
					Set<String> homes = TheAPI.getUser(s.getName()).getKeys("Homes");
					if (!homes.isEmpty() && homes != null)
						c.addAll(StringUtil.copyPartialMatches(args[0], homes, new UnsortedList<>()));
				}
			}
		}
		return c;
	}
}
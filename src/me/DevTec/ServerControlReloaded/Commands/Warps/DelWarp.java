package me.DevTec.ServerControlReloaded.Commands.Warps;


import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;

public class DelWarp implements CommandExecutor, TabCompleter {
	public String warp(String ss) {
		for (String s : Loader.config.getKeys("Warps")) {
			if (s.equalsIgnoreCase(ss)) {
				return s;
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (Loader.has(s, "DelWarp", "Warps")) {
			if (args.length == 0) {
				Loader.Help(s, "DelWarp", "Warps");
				return true;
			}
			if (warp(args[0])!=null) {
				String name = warp(args[0]);
				Loader.config.remove("Warps." + name);
				Loader.config.save();
				Loader.sendMessages(s, "Warp.Deleted", Placeholder.c()
						.add("%warp%", name));
				return true;
			}
			Loader.sendMessages(s, "Warp.NotExist", Placeholder.c()
					.add("%warp%", args[0]));
			return true;
		}
		Loader.noPerms(s, "DelWarp", "Warps");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> c = new UnsortedList<>();
		if (cmd.getName().equalsIgnoreCase("DelWarp") && args.length == 1) {
			if (s.hasPermission("ServerControl.DelWarp")) {
				Set<String> homes = Loader.config.getKeys("Warps");
				if (!homes.isEmpty() && homes != null)
					c.addAll(StringUtil.copyPartialMatches(args[0], homes, new UnsortedList<>()));
			}
		}
		return c;
	}
}
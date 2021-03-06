package me.DevTec.ServerControlReloaded.Commands.Warps;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.API.TeleportLocation;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;

public class Home implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {

		if (s instanceof Player) {
			Player p = (Player) s;
			User d = TheAPI.getUser(p);
			if (Loader.has(s, "Home", "Warps")) {
				if (args.length == 0) {
					if (d.exist("Homes.home")) {
						Position loc = Position.fromString(d.getString("Homes.home"));
						if (loc != null) {
							API.setBack(p);
							if (setting.tp_safe)
								API.safeTeleport((Player)s,loc.toLocation());
							else
								((Player)s).teleport(loc.toLocation());
							Loader.sendMessages(s, "Home.Teleporting", Placeholder.c()
									.add("%home%", "home"));
							return true;
						}
						return true;
					}
					if(!d.getKeys("Homes").isEmpty()) {
						String home = (String) d.getKeys("Homes").toArray()[0];
						Position loc2 = Position.fromString(d.getString("Homes." + home)); 
						API.setBack(p);
						if (loc2 != null) {
							if(setting.tp_safe)
								API.safeTeleport((Player)s,loc2.toLocation());
							else
								((Player)s).teleport(loc2.toLocation());
							Loader.sendMessages(s, "Home.Teleporting", Placeholder.c()
									.add("%home%", home));
							return true;
						}
						Loader.sendMessages(s, "Home.NotExist", Placeholder.c()
								.add("%home%", args[0]));
						return true;
					}
					API.setBack(p);
					API.teleportPlayer(p, TeleportLocation.SPAWN);
					Loader.sendMessages(s, "Home.TpSpawn");
					return true;
				}
				if (args.length == 1) {
					if (d.exist("Homes." + args[0])) {
						Position loc2 = Position.fromString(d.getString("Homes." + args[0]));
						if (loc2 != null) {
							API.setBack(p);
							if(setting.tp_safe)
								API.safeTeleport((Player)s,loc2.toLocation());
							else
								((Player)s).teleport(loc2.toLocation());
							Loader.sendMessages(s, "Home.Teleporting", Placeholder.c()
									.add("%home%", args[0]));
							return true;
						}
					}
					Loader.sendMessages(s, "Home.NotExist", Placeholder.c()
							.add("%home%", args[0]));
					return true;
				}
			}
			Loader.noPerms(s, "Home", "Warps");
			return true;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> c = new UnsortedList<>();
		if (s instanceof Player) {
			if (args.length == 1) {
				if (s.hasPermission("ServerControl.Home")) {
					try {
						c.addAll(StringUtil.copyPartialMatches(args[0], TheAPI.getUser(s.getName()).getKeys("Homes"), new UnsortedList<>()));
					} catch (Exception e) {

					}
				}
			}
		}
		return c;
	}
}
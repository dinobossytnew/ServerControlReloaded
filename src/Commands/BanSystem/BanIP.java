package Commands.BanSystem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ServerControl.API;
import ServerControl.Loader;
import me.DevTec.TheAPI;

public class BanIP implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (API.hasPerm(s, "ServerControl.BanIP")) {
			if (args.length == 0) {
				Loader.Help(s, "/Ban-IP <player> <reason>", "BanSystem.BanIP");
				return true;
			}
			if (args.length == 1) {
				if (TheAPI.getUser(args[0]).getBoolean("Immune")
						|| Bukkit.getOperators().contains(Bukkit.getOfflinePlayer(args[0]))) {
					TheAPI.msg(Loader.s("Prefix") + Loader.s("Immune.NoPunish").replace("%punishment%", "Ban-IP")
							.replace("%target%", args[0]), s);
					return true;
				}
				TheAPI.getPunishmentAPI().banIP(args[0], Loader.config.getString("BanSystem.BanIP.Text")
						.replace("%reason%", Loader.config.getString("BanSystem.BanIP.Reason")));
				return true;

			}
			if (args.length >= 2) {
				if (TheAPI.getUser(args[0]).getBoolean("Immune")
						|| Bukkit.getOperators().contains(Bukkit.getOfflinePlayer(args[0]))) {
					TheAPI.msg(Loader.s("Prefix") + Loader.s("Immune.NoPunish").replace("%punishment%", "Ban-IP")
							.replace("%target%", args[0]), s);
					return true;
				}
				String msg = TheAPI.buildString(args);
				msg = msg.replaceFirst(args[0] + " ", "");
				TheAPI.getPunishmentAPI().banIP(args[0], Loader.config.getString("BanSystem.BanIP.Text")
						.replace("%reason%", Loader.config.getString("BanSystem.BanIP.Reason")));
				if(msg.endsWith("-s")) {
					msg = msg.replace("-s", "");
					Bukkit.broadcast(TheAPI.colorize(Loader.s("BanSystem.Broadcast.Ban").replace("%playername%", args[0]) //TODO - upravit path
							.replace("%reason%", msg).replace("%operator%", s.getName())+" &f[Silent]"
							),"servercontrol.seesilent");
					
					TheAPI.sendMessage(Loader.s("BanSystem.Ban").replace("%playername%", args[0])
							.replace("%reason%", msg).replace("%operator%", s.getName()), s);
					return true;
				}
				Bukkit.broadcastMessage(TheAPI.colorize(Loader.s("BanSystem.Broadcast.Ban").replace("%playername%", args[0])
						.replace("%reason%", msg).replace("%operator%", s.getName())
						));
				TheAPI.sendMessage(Loader.s("BanSystem.Ban").replace("%playername%", args[0])
						.replace("%reason%", msg).replace("%operator%", s.getName()), s);
				return true;
			}
		}
		return true;
	}

}

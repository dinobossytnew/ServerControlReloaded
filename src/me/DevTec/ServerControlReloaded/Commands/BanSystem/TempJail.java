package me.DevTec.ServerControlReloaded.Commands.BanSystem;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.punishmentapi.PunishmentAPI;
import me.devtec.theapi.utils.StringUtils;

public class TempJail implements CommandExecutor, TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1,
			String arg2, String[] arg3) {
		return null;
	}

	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if (Loader.has(s, "TempJail", "BanSystem")) {
			if (args.length == 0) {
				Loader.Help(s, "TempJail", "BanSystem");
				return true;
			}
			if (args.length == 1) {
				if (TheAPI.getUser(args[0]).getBoolean("Immune")
						|| Bukkit.getOperators().contains(Bukkit.getOfflinePlayer(args[0]))) {
					Loader.sendMessages(s, "Immune.NoPunish", Placeholder.c().add("%player%", args[0]));
					return true;
				}
				PunishmentAPI.tempjail(args[0], Loader.config.getString("BanSystem.TempJail.Text").replace("%reason%",
						Loader.config.getString("BanSystem.TempJail.Reason")), StringUtils.getTimeFromString(Loader.config.getString("BanSystem.TempJail.Time")));
				Loader.sendMessages(s, "BanSystem.TempJail.Sender", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", Loader.config.getString("BanSystem.TempJail.Reason")).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(Loader.config.getString("BanSystem.TempJail.Time")))));
				Loader.sendBroadcasts(s, "BanSystem.TempJail.Admins", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", Loader.config.getString("BanSystem.TempJail.Reason")).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(Loader.config.getString("BanSystem.TempJail.Time")))));
				return true;
			}
			if (args.length == 2) {
				if (TheAPI.getUser(args[0]).getBoolean("Immune")
						|| Bukkit.getOperators().contains(Bukkit.getOfflinePlayer(args[0]))) {
					Loader.sendMessages(s, "Immune.NoPunish", Placeholder.c().add("%player%", args[0]));
					return true;
				}
				PunishmentAPI.tempjail(args[0], Loader.config.getString("BanSystem.TempJail.Text").replace("%reason%",
						Loader.config.getString("BanSystem.TempJail.Reason")), StringUtils.getTimeFromString(args[1]));
				Loader.sendMessages(s, "BanSystem.TempJail.Sender", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", Loader.config.getString("BanSystem.TempJail.Reason")).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))));
				Loader.sendBroadcasts(s, "BanSystem.TempJail.Admins", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", Loader.config.getString("BanSystem.TempJail.Reason")).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))));
				return true;
			}
			if (TheAPI.getUser(args[0]).getBoolean("Immune")
					|| Bukkit.getOperators().contains(Bukkit.getOfflinePlayer(args[0]))) {
				Loader.sendMessages(s, "Immune.NoPunish", Placeholder.c().add("%player%", args[0]));
				return true;
			}
			String msg = StringUtils.buildString(2, args);
			if(msg.endsWith("-s")||msg.endsWith("- s")) {
				msg = msg.endsWith("- s")?msg.substring(0, msg.length()-3):msg.substring(0, msg.length()-2);
				PunishmentAPI.tempjail(args[0], Loader.config.getString("BanSystem.TempJail.Text").replace("%reason%",msg), StringUtils.timeFromString(args[1]));
				Loader.sendMessages(s, "BanSystem.TempJail.Sender", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", msg+" &f[Silent]").replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))));
				Loader.sendBroadcasts(s, "BanSystem.TempJail.Admins", Placeholder.c().replace("%operator%", s.getName())
						.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", msg+" &f[Silent]").replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))), "servercontrol.silent");
				return true;
			}
			PunishmentAPI.tempjail(args[0], Loader.config.getString("BanSystem.TempJail.Text").replace("%reason%",msg), StringUtils.timeFromString(args[1]));
			Loader.sendMessages(s, "BanSystem.TempJail.Sender", Placeholder.c().replace("%operator%", s.getName())
					.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", msg).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))));
			Loader.sendBroadcasts(s, "BanSystem.TempJail.Admins", Placeholder.c().replace("%operator%", s.getName())
					.replace("%playername%", args[0]).replace("%player%", args[0]).replace("%reason%", msg).replace("%time%", StringUtils.timeToString(StringUtils.timeFromString(args[1]))));
			return true;
		}
		Loader.noPerms(s, "TempJail", "BanSystem");
		return true;
	}
}

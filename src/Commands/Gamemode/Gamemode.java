package Commands.Gamemode;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.TheAPI;

public class Gamemode implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {

		if (args.length == 0) {
			if (Loader.has(s, "Gamemode", "Gamemode")) {
				Loader.Help(s, "/GameMode <s|c|a|sp> <player>", "Gamemode");
				return true;
			}
			Loader.noPerms(s, "Gamemode", "Gamemode");
			return true;
		}
		String gamemode = null;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0"))
				gamemode = "Survival";
			else

			if (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1"))
				gamemode = "Creative";
			else

			if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2"))
				gamemode = "Adventure";
			else

			if (args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("spectator")
					|| args[0].equalsIgnoreCase("3"))
				gamemode = "Spectator";
			else {
				gamemode = null;

			}
			
			if(TheAPI.isOlderThan(8)) {
				if(gamemode.equals("Spectator")) {
				TheAPI.msg("&cUnsupported GameMode type", s);
				return true;
				}
			}
			
			if(gamemode == null) {
				Loader.sendMessages(s, "Missing.Gamemode", Placeholder.c()
						.add("%gamemode%", args[0]));
				return true;
			}
			if (s instanceof Player) {
				if (Loader.has(s, "Gamemode." + gamemode, "Gamemode")) {
						((Player) s).setGameMode(GameMode.valueOf(gamemode.toUpperCase()));
						Loader.sendMessages(s, "Gamemode.Your.Custom", Placeholder.c()
								.add("%gamemode%", gamemode));
						return true;
				}
				Loader.noPerms(s, "Gamemode." + gamemode, "Gamemode");
				return true;
			}
			Loader.Help(s, "/GameMode " + args[0] + " <player>", "Gamemode");
			return true;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0"))
				gamemode = "Survival";
			else

			if (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1"))
				gamemode = "Creative";
			else

			if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2"))
				gamemode = "Adventure";
			else

			if (args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("spectator")
					|| args[0].equalsIgnoreCase("3"))
				gamemode = "Spectator";
			else {
				gamemode = null;
			}
			if(TheAPI.isOlderThan(8)) {
				if(gamemode.equals("Spectator")) {
				TheAPI.msg("&cUnsupported GameMode type", s);
				return true;
				}
			}
			
			if(gamemode == null) {
				Loader.sendMessages(s, "Missing.Gamemode", Placeholder.c()
						.add("%gamemode%", args[0]));
				return true;
			}
			
			if (Loader.has(s, "Gamemode." + gamemode, "Gamemode")) {
				Player p = TheAPI.getPlayer(args[1]);
					if (p != null) {
						p.setGameMode(GameMode.valueOf(gamemode.toUpperCase()));
						Loader.sendMessages(p, "Gamemode.Other.Custom.Receiver", Placeholder.c()
								.add("%gamemode%", gamemode));
						
						Loader.sendMessages(s, "Gamemode.Other.Custom.Sender", Placeholder.c()
								.add("%player%", p.getName())
								.add("%playername%", p.getDisplayName())
								.add("%gamemode%", gamemode));
						return true;
					}
					Loader.sendMessages(s, "Missing.Player.Offline", Placeholder.c()
							.add("%player%", args[0]));
					return true;
			}
			Loader.noPerms(s, "Gamemode." + gamemode, "Gamemode");
			return true;
		}

		return false;
	}
}
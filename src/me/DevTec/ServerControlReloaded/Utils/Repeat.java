package me.DevTec.ServerControlReloaded.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.TheAPI.SudoType;

public class Repeat {
	public static void a(CommandSender s, String c) {
		for (Player p : TheAPI.getOnlinePlayers()) {
			String r = c.replace("*", p.getName());
			if (s instanceof Player)
				TheAPI.sudo((Player) s, SudoType.COMMAND, r);
			else
				TheAPI.sudoConsole(SudoType.COMMAND, r);
		}
	}
}

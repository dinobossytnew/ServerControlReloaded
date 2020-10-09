package Commands.Tpa;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.User;

public class Tpall implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "TpAll", "TpSystem")) {
			if (s instanceof Player) {
				ArrayList<String> list = new ArrayList<String>();
				for (Player p : TheAPI.getOnlinePlayers()) {
					if (p == s)
						continue;
					User d = TheAPI.getUser(p);
					if (!d.getBoolean("TpBlock." + s.getName()) && !d.getBoolean("TpBlock-Global")
							|| d.getBoolean("TpBlock." + s.getName()) && !d.getBoolean("TpBlock-Global")
									&& s.hasPermission("ServerControl.Tpall.Blocked")
							|| d.getBoolean("TpBlock." + s.getName()) && d.getBoolean("TpBlock-Global")
									&& s.hasPermission("ServerControl.Tpall.Blocked")
							|| !d.getBoolean("TpBlock." + s.getName()) && d.getBoolean("TpBlock-Global")
									&& s.hasPermission("ServerControl.Tpall.Blocked")) {
						list.add(p.getName());
						p.teleport(((Player) s));
					}
				}
				Loader.sendMessages(s, "TpSystem.TpAll", Placeholder.c().replace("%list%",
						list.isEmpty()?"none":StringUtils.join(list, ", ")).replace("%amount%",
								list.size()+""));
				return true;
			}
			return true;
		}
		return true;
	}
}
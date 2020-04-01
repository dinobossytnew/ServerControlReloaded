package Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.API;
import ServerControl.Loader;
import me.Straiker123.TheAPI;

public class PNight implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length==0) {
			if(s instanceof Player) {
			if(API.hasPerm(s, "ServerControl.PlayerNight")) {
				((Player) s).setPlayerTime(13000,true);
				Loader.msg(Loader.s("Prefix")+Loader.s("Time.Night")
				.replace("%world%", ((Player) s).getLocation().getWorld().getName())
						,s);
				return true;
			}return true;}
			Loader.Help(s, "/Night <world>", "Time");
			return true;
		}
		if(args.length==1) {
			if(API.hasPerm(s, "ServerControl.PlayerNight")) {
			if(TheAPI.getPlayer(args[0])!=null) {
				TheAPI.getPlayer(args[0]).setPlayerTime(13000,true);
				Loader.msg(Loader.s("Prefix")+Loader.s("Time.Night")
				.replace("%world%", args[0]),s);
				return true;
			}
			Loader.msg(Loader.s("Prefix")+Loader.s("Time.WorldNotExists")
			.replace("%world%", args[0]),s);return true;
			}return true;}
		return false;
	}

}
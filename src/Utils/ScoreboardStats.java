package Utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

import ServerControl.Loader;
import me.Straiker123.ScoreboardAPIV2;
import me.Straiker123.TheAPI;

public class ScoreboardStats {

	 static HashMap<Player, ScoreboardAPIV2> setup = new HashMap<Player, ScoreboardAPIV2>();
	 public static void createScoreboard(Player p) {
		 if(!setup.containsKey(p))setup.put(p, TheAPI.getScoreboardAPIV2(p));
		 ScoreboardAPIV2 a=setup.get(p);
			String getName = "Name";
			 String getLine = "Lines";
			 if(setting.sb_world ) {
				 if( Loader.scFile.getString("PerWorld."+p.getWorld().getName()+".Name")!=null )
					 getName="PerWorld."+p.getWorld().getName()+".Name";
				 if( Loader.scFile.getString("PerWorld."+p.getWorld().getName()+".Lines")!=null )
					 getLine="PerWorld."+p.getWorld().getName()+".Lines";
			 }
			a.setTitle(Loader.scFile.getString(getName));
			for(String ss: Loader.scFile.getStringList(getLine)) {
				a.addLine(TheAPI.getPlaceholderAPI().setPlaceholders(p,TabList.replace(ss, p)));
			}
			 a.create();
		 }
			 	
	public static void removeScoreboard() {
		for(Player p:TheAPI.getOnlinePlayers()) {
			setup.remove(p);
			 p.setScoreboard(p.getServer().getScoreboardManager().getNewScoreboard());
	}
}}
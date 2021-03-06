package me.DevTec.ServerControlReloaded.Commands.Other;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.Skins.Manager.SkinCallback;
import me.DevTec.ServerControlReloaded.Utils.Skins.Manager.SkinData;
import me.DevTec.ServerControlReloaded.Utils.Skins.Manager.SkinManager;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;

public class Skin implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new UnsortedList<>();
		if(args.length==1) {
			List<String> w = new UnsortedList<>();
			w.add("Reset");
			for(Player d : TheAPI.getOnlinePlayers())
				w.add(d.getName());
			c.addAll(StringUtils.copyPartialMatches(args[0], w));
		}
		if(args.length==2) {
			return null;
		}
		return c;
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length==0) {
			Loader.Help(s, "Skin", "Other");
			return true;
		}
		if(args.length==1) {
			if(s instanceof Player) {
			if(args[0].equalsIgnoreCase("reset")) {
				TheAPI.getUser((Player)s).setAndSave("skin", null);
				SkinManager.generateSkin(s.getName(), new SkinCallback() {
					@Override
					public void run(SkinData data) {
						SkinManager.setSkin(s.getName(), data);
						SkinManager.loadSkin((Player)s, data);
					}
				}, false);
				Loader.sendMessages(s, "Skin.Reset.You");
				return true;
			}
			TheAPI.getUser((Player)s).setAndSave("skin", args[0]);
			SkinManager.generateSkin(args[0], new SkinCallback() {
				@Override
				public void run(SkinData data) {
					SkinManager.setSkin(s.getName(), data);
					SkinManager.loadSkin((Player)s, data);
				}
			}, false);
			Loader.sendMessages(s, "Skin.Set.You", Placeholder.c().add("%skin%", args[0]));
			return true;
			}
			Loader.Help(s, "Skin", "Other");
			return true;
		}
		Player a = TheAPI.getPlayer(args[1]);
		if(a==null) {
			Loader.notOnline(s, args[1]);
			return true;
		}
		if(args[0].equalsIgnoreCase("reset")) {
			TheAPI.getUser(a).setAndSave("skin", null);
			SkinManager.generateSkin(a.getName(), new SkinCallback() {
				@Override
				public void run(SkinData data) {
					SkinManager.setSkin(a.getName(), data);
					SkinManager.loadSkin(a, data);
				}
			}, false);
			Loader.sendMessages(s, "Skin.Reset.Other.Sender", Placeholder.c().add("%player%", a.getName()).add("%playername%", a.getDisplayName()).add("%skin%", args[0]));
			Loader.sendMessages(a, "Skin.Reset.Other.Receiver", Placeholder.c().add("%player%", s.getName()).add("%playername%", s.getName()).add("%skin%", args[0]));
			return true;
		}
		TheAPI.getUser(a).setAndSave("skin", args[0]);
		SkinManager.generateSkin(args[0], new SkinCallback() {
			@Override
			public void run(SkinData data) {
				SkinManager.setSkin(a.getName(), data);
				SkinManager.loadSkin(a, data);
			}
		}, false);
		Loader.sendMessages(s, "Skin.Set.Other.Sender", Placeholder.c().add("%skin%", args[0]).add("%player%", a.getName()).add("%playername%", a.getDisplayName()).add("%skin%", args[0]));
		Loader.sendMessages(a, "Skin.Set.Other.Receiver", Placeholder.c().add("%skin%", args[0]).add("%player%", s.getName()).add("%playername%", s.getName()).add("%skin%", args[0]));
		return true;
	}
}

package me.DevTec.ServerControlReloaded.Events;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.Tasks;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.reflections.Ref;

public class LoginEvent implements Listener {
	private static Object surv = Ref.getNulled(Ref.nms("EnumGamemode"), "SURVIVAL"), spec = Ref.getNulled(Ref.nms("EnumGamemode"), "SPECTATOR");
	private static Object up = Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "UPDATE_GAME_MODE"));
	public static void moveInTab(Player player) {
		Object array = Array.newInstance(Ref.nms("EntityPlayer"), 1);
		Array.set(array, 0, Ref.player(player));
		Object b = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutPlayerInfo"), Ref.nms("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), array.getClass()), up, array);
		@SuppressWarnings("unchecked")
		List<Object> bList = (List<Object>) Ref.get(b, "b");
		int cc = 0;
		for(Object o : bList) { //that's bad
			Ref.set(o, "c", TheAPI.hasVanish(player.getName()) && setting.tab_vanish ? (spec==null?surv:spec) : surv);
			bList.set(cc++, o);
		}
		Ref.set(b, "b", bList);
		for(Player p : TheAPI.getOnlinePlayers())
			if(p!=player)
			Ref.sendPacket(p, b);
	}

	public Loader plugin = Loader.getInstance;

	private void bc(Player p) {
		if (setting.vip_join) {
			TheAPI.broadcastMessage(
					Loader.config.getString("Options.VIPSlots.Text.BroadcastVIPJoin")
							.replace("%players_max%", String.valueOf(TheAPI.getMaxPlayers()
									+ (setting.vip_add ? Loader.config.getInt("Options.VIPSlots.SlotsToAdd") : 0)))
							.replace("%online%", String.valueOf(TheAPI.getOnlinePlayers().size()))
							.replace("%player%", p.getName()).replace("%playername%", p.getDisplayName())
							.replace("%prefix%", setting.prefix)
							.replace("%time%", setting.format_time.format(new Date()))
							.replace("%date%", setting.format_date.format(new Date()))
							.replace("%date-time%", setting.format_date_time.format(new Date())));
		}
	}

	private static String kickString= StringUtils.join(Loader.config.getStringList("Options.Maintenance.KickMessages"), "\n");
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void JoinEvent(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		Loader.setupChatFormat(p);
		if (setting.lock_server && !Loader.has(p, "Other", "Maintenance", "Bypass")) {
			e.disallow(Result.KICK_OTHER, TheAPI.colorize(kickString.replace("%player%", p.getName()).replace("%playername%", p.getDisplayName())));
			return;
		}
		if (setting.vip && TheAPI.getMaxPlayers() == TheAPI.getOnlinePlayers().size() - 1) {
			Config f = Loader.config;
			boolean has = p.hasPermission("SCR.Other.JoinFullServer");
			int max = TheAPI.getMaxPlayers() + (setting.vip_add ? f.getInt("Options.VIPSlots.SlotsToAdd") : 0);
			Player randomPlayer = Tasks.players.isEmpty() ? null : TheAPI.getRandomPlayer();
			if (has) {
				if (TheAPI.getMaxPlayers() > max && randomPlayer == null) {
					e.disallow(Result.KICK_FULL, TheAPI.colorize(f.getString("Options.VIPSlots.FullServer")));
					return;
				}
				if (setting.vip_kick) {
					if (TheAPI.getMaxPlayers() > max && randomPlayer != null) {
						Tasks.players.remove(randomPlayer.getName());
						randomPlayer.kickPlayer(TheAPI.colorize(f.getString("Options.VIPSlots.Text.Kick")));
						bc(p);
						e.allow();
						return;
					}
				}
			} else if (TheAPI.getOnlinePlayers().size() >= TheAPI.getMaxPlayers()) {
				e.disallow(Result.KICK_FULL, TheAPI.colorize(f.getString("Options.VIPSlots.Text.Kick")));
				return;
			}
		}
	}
}
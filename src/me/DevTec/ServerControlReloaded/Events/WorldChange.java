package me.DevTec.ServerControlReloaded.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.Utils.SPlayer;
import me.DevTec.ServerControlReloaded.Utils.setting;

public class WorldChange implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSleep(PlayerBedEnterEvent e) {
		if (setting.singeplayersleep) {
			e.getPlayer().getWorld().setTime(1000);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerWorldChangeEvent(PlayerChangedWorldEvent e) {
		SPlayer a = API.getSPlayer(e.getPlayer());
		a.createEconomyAccount();
		if (a.hasFlyEnabled())
			a.enableFly();
		if (a.hasTempFlyEnabled())
			a.enableTempFly();
		if (a.hasGodEnabled())
			a.enableGod();
		a.setGamamode();

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChangeGamamode(PlayerGameModeChangeEvent e) {
		SPlayer a = API.getSPlayer(e.getPlayer());
		if (a.hasFlyEnabled())
			a.enableFly();
		if (a.hasTempFlyEnabled())
			a.enableTempFly();
		if (a.hasGodEnabled())
			a.enableGod();
	}
}
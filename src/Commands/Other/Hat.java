package Commands.Other;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.TheAPI;

public class Hat implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "Hat", "Other")) {
			if (s instanceof Player) {
				Player p = (Player) s;
				if (p.getItemInHand().getType() == Material.AIR) {
					Loader.sendMessages(s, "Missing.HandEmpty");
					return true;
				}
				Inventory inv = p.getInventory();
				boolean check = false;
				for (ItemStack item : inv.getStorageContents()) {
					if (item == null) {
						check = true;
						break;
					}
				}				
				if (args.length == 0) {					
					if (!check) {
						Loader.sendMessages(p, "Hat.InvFull.You");
						return true;
					}
					if (p.getInventory().getHelmet() != null)
						p.getInventory().addItem(p.getInventory().getHelmet()); 
					p.getInventory().setHelmet(p.getEquipment().getItemInMainHand());
					p.getInventory().setItemInHand(new ItemStack(Material.AIR));
					Loader.sendMessages(s, "Hat.Equipped.You", Placeholder.c().replace("%item%", p.getInventory().getHelmet().getType().name()));
					return true;
				}
				if (Loader.has(s, "Hat", "Other", "Other")) {
					if (args.length == 1) {
				Player t = TheAPI.getPlayer(args[0]);
				if (t == null) {
					Loader.notOnline(s,args[0]);
					return true;
				}
				boolean check2=false;
				inv = t.getInventory();
				for (ItemStack item : inv.getStorageContents()) {
					if(item==null) {
						check2=true;
					}
				}
				if(!check2) {
					Loader.sendMessages(p, "Hat.InvFull.Other", Placeholder.c()
							.add("%player%", t.getName())
							.add("%playername%", t.getDisplayName()));
					return true;
				}				
				if (t.getInventory().getHelmet() != null)					
					Loader.sendMessages(t, "Hat.Invfull.Other", Placeholder.c()
								.add("%player%", t.getName())
								.add("%playername%", t.getDisplayName()));					
					t.getInventory().addItem(t.getInventory().getHelmet());
				t.getInventory().setHelmet(p.getEquipment().getItemInMainHand());
				p.getInventory().setItemInHand(new ItemStack(Material.AIR));
				Loader.sendMessages(s, "Hat.Equipped.Other.Sender", Placeholder.c().replace("%player%", t.getName()).replace("%playername%", t.getDisplayName()).replace("%item%", t.getInventory().getHelmet().getType().name()));
				Loader.sendMessages(t, "Hat.Equipped.Other.Receiver", Placeholder.c().replace("%player%", s.getName()).replace("%playername%", s.getName()).replace("%item%", t.getInventory().getHelmet().getType().name()));
				return true;
			}
		}
				Loader.noPerms(s, "Hat", "Other", "Other");
				return true;
			}
			return true;
		}
		Loader.noPerms(s, "Hat", "Other");
		return true;
	}

}

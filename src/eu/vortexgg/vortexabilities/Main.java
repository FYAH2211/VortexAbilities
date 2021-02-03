package eu.vortexgg.vortexabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import eu.vortexgg.vortexabilities.api.ItemUtilities;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.AbilityManager;

public class Main extends JavaPlugin implements CommandExecutor, TabCompleter {
    public FileConfiguration c;
    public static Main inst;
	public AbilityManager abilityManager;
    
    public void onEnable() {
    	(inst = this).saveDefaultConfig();
    	c = getConfig();
    	abilityManager = new AbilityManager(this);
    	ConsoleCommandSender log = Bukkit.getConsoleSender();
    	getCommand("vabilities").setExecutor(this);
    	getCommand("vabilities").setTabCompleter(this);
    	log.sendMessage("§2VortexAbilities §fv" + getDescription().getVersion() + " enabled!");
    }
    
    public void onDisable() {
    	ConsoleCommandSender log = Bukkit.getConsoleSender();
    	log.sendMessage("§2VortexAbilities §fv" + getDescription().getVersion() + " disabled!");
    }
    
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
    	if (args.length == 0 && s instanceof Player) {
    		Player p = (Player)s;
    		p.openInventory(abilityInv());
    		return true;
    	}
		final String p = "§2§lVortexAbilities §7\u23a2 ";
    	if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
    		s.sendMessage("");
    		s.sendMessage("         §2§l VortexAbilities");
    		s.sendMessage("/abilities §8- §7Show inventory of Abilities.");
    		if(s.hasPermission("vortexabilities.admin"))
    			s.sendMessage("/vabilities reload §8- §7Reload the config.");
    		if(s.hasPermission("vortexabilities.give")) {
    			s.sendMessage("/vabilities give <playerName> <abilityName> <amount> §8- §7Give an Ability to player.");
    			s.sendMessage("/vabilities giveall <abilityName> <amount> §8- §7Give an Ability to player.");
    			s.sendMessage("/vabilities list §8- §7Show all Ability names.");
    			s.sendMessage("/vabilities removecooldown <playerName> <abilityName> §8- §7Remove ability cooldown from player.");
    		}
    		s.sendMessage("");
    	}
    	if (s instanceof ConsoleCommandSender || (s instanceof Player && s.hasPermission("vortexabilities.admin"))) {
    		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
    			reloadConfig();
    			c = getConfig();
    			s.sendMessage(p+"§aConfig reloaded!");
    			return true;
    		}
    		if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
    			s.sendMessage("");
    			s.sendMessage("         §2§lVortexAbilities");
    			for(Ability f : abilityManager.getAbilities()) {
    				s.sendMessage("§7» "+f.name.toLowerCase());
    			}
    			s.sendMessage("");
    		}
    		if(args.length > 0 && args.length < 4 && args[0].equalsIgnoreCase("give"))
    			s.sendMessage(p+"§cUse: /vabilities give <playerName> <abilityTag> <amount>");
    		if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
    			Player t = Bukkit.getPlayer(args[1]);
    			if (t == null) {
    				s.sendMessage(p+"§cPlayer '" + args[1] + "' not found.");
    				return true;
    			}
    			Ability a = abilityManager.getAbilityByName(args[2]);
    			if (a == null) {
    				s.sendMessage(p+"§cAbility '" + args[2] + "' not found. Check out: /vabilities list");
    				return true;
    			}
    			Integer f = U.tryParseInt(args[3]);
    			if (f == null) {
    				s.sendMessage("§cUse: /vabilities give <player> <abilityname> <amount>");
    				return true;
    			}
    			if (t.getInventory().firstEmpty() == -1) {
    				s.sendMessage(p+"§f" + t.getName() + "§c does not have enougth space in inventory.");
    				return true;
    			}
    			s.sendMessage(p+"§fYou gived §ax"+f+" "+WordUtils.capitalizeFully(args[2])+" §fto §7"+args[1]+"§f.");
    			ItemStack j = a.item;
    			j.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    			j.setAmount(f);
    			t.getInventory().addItem(j);
    		}
    		if (args.length == 3 && args[0].equalsIgnoreCase("giveall")) {
    			Ability a = abilityManager.getAbilityByName(args[1]);
    			if (a == null) {
    				s.sendMessage(p+"§cAbility '" + args[1] + "' not found. Check out: /vabilities list");
    				return true;
    			}
    			Integer f = U.tryParseInt(args[2]);
    			if (f == null) {
    				s.sendMessage("§cUse: /vabilities give <player> <abilityname> <amount>");
    				return true;
    			}
    			s.sendMessage(p+"§fYou gived §ax"+f+" "+WordUtils.capitalizeFully(args[1])+" §fto whole server.");
    			ItemStack j = a.item;
    			j.setAmount(f);
    			j.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    			for(Player pp : Bukkit.getOnlinePlayers()) {
    				if(pp.getInventory().firstEmpty() == -1) {
    					pp.getWorld().dropItem(pp.getLocation(), j);
    					continue;
    				}
    				pp.getInventory().addItem(j);
    			}
    		}
    		if (args.length == 3 && args[0].equalsIgnoreCase("removecooldown")) {
    			Player t = Bukkit.getPlayer(args[1]);
    			if (t == null) {
    				s.sendMessage(p+"§cPlayer '" + args[1] + "' not found.");
    				return true;
    			}
    			Ability a = abilityManager.getAbilityByName(args[2]);
    			if (a == null) {
    				s.sendMessage(p+"§cAbility '" + args[2] + "' not found. Check out: /vabilities list");
    				return true;
    			}
    			if(a.isOnCooldown(t.getUniqueId())) {
    				a.removeCooldown(t.getUniqueId());
    				s.sendMessage(p+"§fYou have removed cooldown of §7"+a.name+" §ffor §a"+args[1]+"§f!");
    			} else {
    				s.sendMessage(p+"§cPlayer arent on cooldown.");
    			}
    		}
    	} else {
    		s.sendMessage(p+"§cNo permission.");
    	}
    	return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] args) {
    	if(args.length == 3) {
    		List<String> result = new ArrayList<String>();
    		for (Ability ability : abilityManager.getAbilities())
    			result.add(Pattern.compile("\\s").matcher(ability.name).replaceAll(""));
    		return result;
    	}
    	return Collections.emptyList();
    }

    
    public Inventory abilityInv() {
        String l = "AbilitiesInventory.";
        Inventory inv = Bukkit.createInventory(null, c.getInt(l + "size"), U.c(c.getString(l + "name")));
        HashMap<ItemStack, Integer> items = new HashMap<ItemStack, Integer>();
        c.getStringList(l + "items").forEach(str -> items.putAll(ItemUtilities.deserialize(str)));
        items.forEach((item, slot) -> inv.setItem((int)slot, item));
        return inv;
    }
    
    public final boolean isAdmin() {
        return c.getBoolean("Settings.removeCooldownForAdmins");
    }

}

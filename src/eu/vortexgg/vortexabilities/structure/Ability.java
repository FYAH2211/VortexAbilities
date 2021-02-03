package eu.vortexgg.vortexabilities.structure;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;

public abstract class Ability {
	
	public String name;
	public String con;
	public ItemStack item;
	public HashMap<UUID, Long> cooldownMap;
	
	public Ability(String configSection, String name, ItemStack item) {
		this.con = configSection;
		this.name = name;
		this.item = item;
		cooldownMap = new HashMap<UUID, Long>();
	}
	
    public boolean isOnCooldown(UUID uuid) {
        return cooldownMap.get(uuid) != null && cooldownMap.get(uuid) + Main.inst.c.getInt(con + "cooldown") * 1000 > System.currentTimeMillis();
    }
    
    public int getCooldownTime(UUID uuid) {
        int f = (int)(System.currentTimeMillis() - cooldownMap.get(uuid));
        return Main.inst.c.getInt(con + "cooldown") - f / 1000;
    }
    
    public void addCooldown(Player p, int seconds, boolean bypassByPermission) {
        if (seconds > 0 && (!p.hasPermission("vortexabilities.admin") || (p.hasPermission("vortexabilities.admin") && !bypassByPermission)))
        	cooldownMap.put(p.getUniqueId(), System.currentTimeMillis());
    }
    
    public void addCooldown(Player p, int seconds) {
    	addCooldown(p, seconds, Main.inst.isAdmin());
    }
    
    public void addCooldown(Player p) {
    	addCooldown(p, Main.inst.c.getInt(con+"cooldown"), Main.inst.isAdmin());
    }
    
    public void removeCooldown(UUID uuid) {
    	if(cooldownMap.containsKey(uuid))
    		cooldownMap.remove(uuid);
    }
    
    public boolean isSimilar(ItemStack item) {
    	return item.getType() == this.item.getType() && item.getItemMeta().getDisplayName().equals(this.item.getItemMeta().getDisplayName()) && item.containsEnchantment(Enchantment.DURABILITY);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (Main.inst.c.getBoolean("Settings.removeCooldownOnQuit")) {
            UUID p = e.getPlayer().getUniqueId();
            if (cooldownMap.containsKey(p))
            	cooldownMap.remove(p);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
        	Player p = e.getPlayer();
            UUID u = p.getUniqueId();
            if (e.getAction() == Action.LEFT_CLICK_BLOCK && isOnCooldown(u) && !StringUtils.isNullOrEmpty(Main.inst.c.getString(con + "cooldownMessage")))
                p.sendMessage(U.c(Main.inst.c.getString(con + "cooldownMessage")).replaceAll("%time%", String.valueOf(getCooldownTime(u))));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e) {
        if (Main.inst.c.getBoolean("Settings.removeCooldownOnQuit")) {
            UUID p = e.getPlayer().getUniqueId();
            if (cooldownMap.containsKey(p))
            	cooldownMap.remove(p);
        }
    }
    
}

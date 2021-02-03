package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class WeaponBreaker extends Ability implements Listener {
    public HashMap<String, Integer> hits;
    public HashMap<UUID, Long> cantAttack = new HashMap<UUID, Long>();
    private Main m;
    
    public WeaponBreaker(Main m) {
        super("Abilities.WeaponBreaker.", "WBREAKER", new VItemStack(Material.getMaterial(m.c.getString("Abilities.WeaponBreaker.type").toUpperCase()), m.c.getString("Abilities.WeaponBreaker.displayname"), m.c.getStringList("Abilities.WeaponBreaker.lore"), (short)m.c.getInt("Abilities.WeaponBreaker.data")));
    	this.m = m;
        hits = new HashMap<String, Integer>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
    	Entity da = e.getDamager();
    	Entity aa = e.getEntity();
        if (da instanceof Player) {
            Player p = (Player)da;
        	UUID u = p.getUniqueId();
        	if(aa instanceof Player) {
	            ItemStack item = p.getItemInHand();
	            if (Utils.isShit(item))
	                return;
	            Player d = (Player)aa;
	            if (isSimilar(item)) {
	                if (!isOnCooldown(u)) {
	                    if (hits.containsKey(p.getName())) {
	                        hits.put(p.getName(), hits.get(p.getName()) + 1);
	                    } else {
	                        hits.put(p.getName(), 1);
	                        Bukkit.getScheduler().runTaskLater(m, () -> {
	                            if (p != null && hits.containsKey(p.getName())) {
	                                hits.remove(p.getName());
	                            }
	                        }, m.c.getInt(con + "resetHits") * 20);
	                    }
	                    if (hits.get(p.getName()) >= m.c.getInt(con + "needHits")) {
	                        VAPI.useAbility(p, d, con);
	                        addCooldown(p);
	                        cantAttack.put(d.getUniqueId(), System.currentTimeMillis() + (m.c.getInt(con +"useTime") * 1000));
	                        hits.remove(p.getName());
	                        U.remove1Item(p);
	                    }
	                } else {
	                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
	                }
	            }
	        }
        	if(aa instanceof LivingEntity) {
        		if(cantAttack.get(u) != null && System.currentTimeMillis() <= cantAttack.get(u)) {
        			e.setCancelled(true);
        			p.sendMessage(U.c(m.c.getString("cantAttack").replaceAll("%time%", U.getRemaining(cantAttack.get(u) - System.currentTimeMillis(), true, true))));
        		}
        	}
        }
    }

}

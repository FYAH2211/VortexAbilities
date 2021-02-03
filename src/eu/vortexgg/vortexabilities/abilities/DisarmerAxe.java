package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class DisarmerAxe extends Ability implements Listener {
    public HashMap<String, Integer> hits;
    public String con;
    public Material type;
    public int data;
    public String name;
    private Main m;
    
    public DisarmerAxe(Main m) {
        super("Abilities.DisarmerAxe.", "DISARMERAXE", new VItemStack(Material.getMaterial(m.c.getString("Abilities.DisarmerAxe.type").toUpperCase()), m.c.getString("Abilities.DisarmerAxe.displayname"), m.c.getStringList("Abilities.DisarmerAxe.lore"), (short)m.c.getInt("Abilities.DisarmerAxe.data")));
    	this.m = m;
        hits = new HashMap<String, Integer>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            Player d = (Player)entity;
            Player p = (Player)damager;
            ItemStack item = p.getItemInHand();
            if (Utils.isShit(item))  return;
            if (isSimilar(item)) {
                UUID u = p.getUniqueId();
                if (!isOnCooldown(u)) {
                    if (d.getInventory().getHelmet() == null || d.getInventory().getHelmet().getType() != Material.DIAMOND_HELMET) {
                        p.sendMessage(U.c(m.c.getString(con + "noHelmet").replace("%enemy_displayname%", d.getDisplayName().replace("%enemy_name%", d.getName()))));
                        return;
                    }
                    if (hits.containsKey(p.getName())) {
                        hits.put(p.getName(), hits.get(p.getName()) + 1);
                    } else {
                        hits.put(p.getName(), 1);
                    }
                    if (hits.get(p.getName()) >= m.c.getInt(con + "needHits")) {
                        VAPI.useAbility(p, d, con);
                        Bukkit.getScheduler().runTaskLater(m, () -> {
                            if (d.isOnline()) {
                                if (d.getInventory().getHelmet() != null && d.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET) {
                                    if (U.isntFullInv(d)) {
                                        d.getInventory().addItem(d.getEquipment().getHelmet());
                                    } else {
                                        d.getWorld().dropItemNaturally(d.getLocation(), d.getEquipment().getHelmet());
                                    }
                                    d.getEquipment().setHelmet(null);
                                }
                            }
                        }, (m.c.getInt(con + "secondsToUnequip") * 20));
        	            addCooldown(p);
                        hits.remove(p.getName());
                        U.remove1Item(p);
                    }
                } else {
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                }
            }
        }
    }

}

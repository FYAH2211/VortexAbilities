package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

public class AimKick extends Ability implements Listener {
    public HashMap<String, Integer> hits;
    private Main m;	

    public AimKick(Main m) {
        super("Abilities.AimKick.", "AIMKICK", new VItemStack(Material.getMaterial(m.c.getString("Abilities.AimKick.type").toUpperCase()), m.c.getString("Abilities.AimKick.displayname"), m.c.getStringList("Abilities.AimKick.lore"), (short)m.c.getInt("Abilities.AimKick.data")));
    	this.m = m;
    	hits = new HashMap<String, Integer>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            Player d = (Player)entity;
            Player p = (Player)damager;
            ItemStack item = p.getItemInHand();
            if (Utils.isShit(item))
                return;	
            UUID up = p.getUniqueId();
            if (isSimilar(item)) {
                if (!isOnCooldown(p.getUniqueId())) {
                    if (hits.containsKey(p.getName())) {
                        hits.put(p.getName(), hits.get(p.getName()) + 1);
                    } else {
                        hits.put(p.getName(), 1);
                        Bukkit.getScheduler().runTaskLater(m, () -> {
                            if (p != null && hits.containsKey(p.getName()))
                                hits.remove(p.getName());
                        }, (m.c.getInt(con + "resetHits") * 20));
                    }
                    if (hits.get(p.getName()) >= m.c.getInt(con + "needHits")) {
                        hits.remove(p.getName());
                        Location orin = d.getLocation();
                        d.teleport(new Location(orin.getWorld(),orin.getX(),orin.getY(),orin.getZ(),orin.getYaw()-m.c.getInt(con+"headDegrees"),orin.getPitch()));
                        VAPI.useAbility(p, d, con);
                        addCooldown(p);
                        U.remove1Item(p);
                    }
                } else {
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(up))));
                }
            }
        }
    }

}

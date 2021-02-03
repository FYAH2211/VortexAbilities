package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class ConfusionStick extends Ability implements Listener {
    public HashMap<String, Integer> hits;
    private Main m;
    
    public ConfusionStick(Main m) {
        super("Abilities.ConfusionStick.", "CONFUSIONSTICK", new VItemStack(Material.getMaterial(m.c.getString("Abilities.ConfusionStick.type").toUpperCase()), m.c.getString("Abilities.ConfusionStick.displayname"), m.c.getStringList("Abilities.ConfusionStick.lore"), (short)m.c.getInt("Abilities.ConfusionStick.data")));
    	this.m = m;
        hits = new HashMap<String, Integer>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player)e.getDamager();
            Player d = (Player)e.getEntity();
            ItemStack item = p.getItemInHand();
            if (Utils.isShit(item))
                return;
            if (isSimilar(item)) {
                UUID u = p.getUniqueId();
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

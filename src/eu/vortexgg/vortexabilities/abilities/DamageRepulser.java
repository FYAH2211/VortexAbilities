package eu.vortexgg.vortexabilities.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class DamageRepulser extends Ability implements Listener {
    public List<String> repulsing;
    private Main m;
    
    public DamageRepulser(Main m) {
        super("Abilities.DamageRepulser.", "DAMAGEREPULSER", new VItemStack(Material.getMaterial(m.c.getString("Abilities.DamageRepulser.type").toUpperCase()), m.c.getString("Abilities.DamageRepulser.displayname"), m.c.getStringList("Abilities.DamageRepulser.lore"), (short)m.c.getInt("Abilities.DamageRepulser.data")));
    	this.m = m;
    	repulsing = new ArrayList<String>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
            UUID u = p.getUniqueId();
            if (e.getAction().toString().contains("RIGHT")) {
                if (isOnCooldown(u)) {
                    e.setCancelled(true);
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                    return;
                }
                VAPI.useAbility(p, con);
                repulsing.add(p.getName());
                
	            Bukkit.getScheduler().runTaskLater(m, () -> {
	            	if(repulsing.contains(p.getName())) 
	            		repulsing.remove(p.getName());
	            }, (m.c.getInt(con + "useTime") * 20));
	            addCooldown(p);
                U.remove1Item(p);
            }	
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
    	if(e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity) {
    		Player p = (Player) e.getEntity();
    		if(repulsing.contains(p.getName())) {
    			e.setCancelled(true);
        		LivingEntity d = (LivingEntity) e.getDamager();
        		d.damage(e.getDamage());
        		if(d instanceof Player) {
	                String msgToHit = U.r(m.c.getString(con + "enemyOnTryToAttackMessage"),p,null);
	                if (!StringUtils.isNullOrEmpty(msgToHit))
	                    ((Player)d).sendMessage(msgToHit);
        		}
    		}
    	}
    }

}

package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.Particles;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class Telekinesis extends Ability implements Listener {
    private Main m;
    
    public Telekinesis(Main m) {
        super("Abilities.Telekinesis.", "TELEKINESIS", new VItemStack(Material.getMaterial(m.c.getString("Abilities.Telekinesis.type").toUpperCase()), m.c.getString("Abilities.Telekinesis.displayname"), m.c.getStringList("Abilities.Telekinesis.lore"), (short)m.c.getInt("Abilities.Telekinesis.data")));
    	this.m = m;
        m.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
            if (e.getAction().toString().contains("RIGHT")) {
        		LivingEntity entity = U.rayTraceEntity(p, m.c.getInt(con+"distanceBetweenPlayers"));
        		if (entity == null) return; 
        		if(!(entity instanceof LivingEntity)) return;
        		if(entity instanceof Player) {
                	UUID u = p.getUniqueId();
	                if (isOnCooldown(u)) {
	                    e.setCancelled(true);
	                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
	                    return;
	                }
	                VAPI.useAbility(p, (Player)entity, con);
                    addCooldown(p);
	                U.remove1Item(p);
	    			(new BukkitRunnable() {
	    				private double timer = 2 * m.c.getDouble(con+"useTime");
	    				public void run() {
	    					if (timer-- >= 0) {
	    						Particles.CLOUD.play(entity.getLocation(), 0.1F, 0.3F, 0.3F, 0, 10);
	    						entity.setVelocity(new Vector(0, m.c.getDouble(con+"VectorY"), 0));
	    					} else {
	    						cancel();
	    					}
	    				}
	    			}).runTaskTimer(Main.inst, 0, 3);
        		}
            }	
        }
    }

}

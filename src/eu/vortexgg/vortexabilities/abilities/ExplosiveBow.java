package eu.vortexgg.vortexabilities.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class ExplosiveBow extends Ability implements Listener {
    private Main m;
    
    public ExplosiveBow(Main m) {
        super("Abilities.ExplosiveBow.", "EXPLOSIVEBOW", new VItemStack(Material.getMaterial(m.c.getString("Abilities.ExplosiveBow.type").toUpperCase()), m.c.getString("Abilities.ExplosiveBow.displayname"), m.c.getStringList("Abilities.ExplosiveBow.lore"), (short)m.c.getInt("Abilities.ExplosiveBow.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    	
    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
    	Entity shooter = e.getEntity();
        if (shooter instanceof Player) {
        	Player p = (Player)shooter;
        	ItemStack i = p.getItemInHand();
        	if(Utils.isShit(i)) return;
        	if(isSimilar(i)) {
	    		if(isOnCooldown(p.getUniqueId())) {
	                e.setCancelled(true);
	                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(p.getUniqueId()))));
	                return;
	    		}
        		e.getProjectile().setMetadata("exp", new FixedMetadataValue(m, true));
                addCooldown(p);
        	}
        }
	}

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
    	Entity ab = e.getEntity();
    	if(ab instanceof Arrow) {
    		if(ab instanceof Player) {
	    		if (ab.hasMetadata("exp")) {
	    			Location l = ab.getLocation();
	    			VAPI.useAbility((Player)((Arrow)ab).getShooter(), con);
	    			ab.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 1F, false, false);
	    			double a = m.c.getDouble(con+"extraExplosionRadius");
	    			if(a > 0)
	    				for(Entity de : ab.getNearbyEntities(a, a, a))
	    					if(de instanceof LivingEntity)
	    						((LivingEntity) de).damage(m.c.getDouble(con+"extraExplosionDamage"));
	    		}
    		}
    	}
    }

}

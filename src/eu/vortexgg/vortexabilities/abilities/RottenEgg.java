package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class RottenEgg extends Ability implements Listener {

    private Main m;
    
    public RottenEgg(Main m) {
        super("Abilities.RottenEgg.", "ROTTENEGG", new VItemStack(Material.getMaterial(m.c.getString("Abilities.RottenEgg.type").toUpperCase()), m.c.getString("Abilities.RottenEgg.displayname"), m.c.getStringList("Abilities.RottenEgg.lore"), (short)m.c.getInt("Abilities.RottenEgg.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onSwitcherLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Egg) || !(e.getEntity().getShooter() instanceof Player))
            return;
        Egg egg = (Egg)e.getEntity();
        Player p = (Player)egg.getShooter();
        ItemStack item = p.getItemInHand()	;
        if (Utils.isShit(item)) return;
        if (isSimilar(item)) {
        	UUID u = p.getUniqueId();
            if (isOnCooldown(u)) {
                e.setCancelled(true);
                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                return;
            }
            egg.setMetadata("egg", new FixedMetadataValue(m, true));
            addCooldown(p);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEgg(EntityDamageByEntityEvent e) {
        Entity en = e.getEntity();
        Entity da = e.getDamager();
        if (en instanceof Player) {
            Player hitplayer = (Player)en;
            if (da instanceof Egg) {
                Egg s = (Egg)da;
                if (s.getShooter() instanceof Player && s.hasMetadata("egg")) {
                	Player shooter = (Player)s.getShooter();
                    if(hitplayer.getLocation().distance(shooter.getLocation()) <= m.c.getDouble(con+"distanceBetweenPlayers")) {
                    	VAPI.useAbility(shooter, hitplayer, con);
                    } else {
                        String msgToShooter = U.r(m.c.getString(con + "distanceEnemyMessage"),shooter,hitplayer);
                        String msgToHit = U.r(m.c.getString(con + "distanceShooterMessage"),shooter,hitplayer);
                        if (msgToShooter != null)
                            shooter.sendMessage(msgToShooter);
                        if (msgToHit != null)
                            hitplayer.sendMessage(msgToHit);
                    }
                }
            }
        }
    }

}

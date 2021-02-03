package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
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

public class SwitcherSnowball extends Ability implements Listener {

    private Main m;
    
    public SwitcherSnowball(Main m) {
        super("Abilities.SwitcherSnowball.", "SWITCHER", new VItemStack(Material.getMaterial(m.c.getString("Abilities.SwitcherSnowball.type").toUpperCase()), m.c.getString("Abilities.SwitcherSnowball.displayname"), m.c.getStringList("Abilities.SwitcherSnowball.lore"), (short)m.c.getInt("Abilities.SwitcherSnowball.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onSwitcherLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball) || !(e.getEntity().getShooter() instanceof Player))
            return;
        Snowball snowBall = (Snowball)e.getEntity();
        Player p = (Player)snowBall.getShooter();
        ItemStack item = p.getItemInHand();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
        	UUID u = p.getUniqueId();
            if (isOnCooldown(u)) {
                e.setCancelled(true);
                item.setAmount(item.getAmount() + 1);
                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                return;
            }
            snowBall.setMetadata("sb", new FixedMetadataValue(m, true));
            addCooldown(p);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamageBySnowball(EntityDamageByEntityEvent e) {
        Entity en = e.getEntity();
        Entity da = e.getDamager();
        if (en instanceof Player) {
            Player hitplayer = (Player)en;
            if (da instanceof Snowball) {
                Snowball s = (Snowball)da;
                if (s.getShooter() instanceof Player && s.hasMetadata("sb")) {
                    Player shooter = (Player)s.getShooter();
                    if(en.getLocation().distance(da.getLocation()) <= m.c.getDouble(con+"distanceBetweenPlayers")) {
                        VAPI.useAbility(shooter, hitplayer, con);
                        Location hitted = hitplayer.getLocation();
                        Location shooted = shooter.getLocation();
                        shooter.teleport(hitted);
                        hitplayer.teleport(shooted);
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

package eu.vortexgg.vortexabilities.abilities;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class RocketBooster extends Ability implements Listener {
	
    public ArrayList<String> noDamage;
    private Main m;
    
    public RocketBooster(Main m) {
        super("Abilities.RocketBooster.", "ROCKETBOOSTER", new VItemStack(Material.getMaterial(m.c.getString("Abilities.RocketBooster.type").toUpperCase()), m.c.getString("Abilities.RocketBooster.displayname"), m.c.getStringList("Abilities.RocketBooster.lore"), (short)m.c.getInt("Abilities.RocketBooster.data")));
    	this.m = m;
        noDamage = new ArrayList<String>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            	UUID u = p.getUniqueId();
                if (isOnCooldown(u)) {
                    e.setCancelled(true);
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                    return;
                }
                VAPI.useAbility(p, con);
                if(m.c.getBoolean(con+"noDamageOnBoost"))
                	noDamage.add(p.getName());
                p.setVelocity(p.getLocation().getDirection().setY(0).multiply(m.c.getDouble(con + "rocketBoost") * 0.8).add(new Vector(0, 2, 0)));
                addCooldown(p);
                U.remove1Item(p);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
    	if (e.getCause() != EntityDamageEvent.DamageCause.FALL)
    		return; 
    	if (!(e.getEntity() instanceof Player))
    		return; 
    	Player p = (Player)e.getEntity();
    	if (!noDamage.contains(p.getName()))
    		return; 
    	e.setCancelled(true);
    	noDamage.remove(p.getName());
    }

}

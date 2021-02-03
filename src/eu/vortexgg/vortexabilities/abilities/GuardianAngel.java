package eu.vortexgg.vortexabilities.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class GuardianAngel extends Ability implements Listener {
    public List<String> guarded;
    private Main m;
    
    public GuardianAngel(Main m) {
        super("Abilities.GuardianAngel.", "GANGEL", new VItemStack(Material.getMaterial(m.c.getString("Abilities.GuardianAngel.type").toUpperCase()), m.c.getString("Abilities.GuardianAngel.displayname"), m.c.getStringList("Abilities.GuardianAngel.lore"), (short)m.c.getInt("Abilities.GuardianAngel.data")));
    	this.m = m;
        guarded = new ArrayList<String>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
        	final Action a = e.getAction();
            if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            	UUID u = p.getUniqueId();
	            if (isGuarded(p)) {
	            	if(!StringUtils.isNullOrEmpty(m.c.getString(con+"alreadyGuarded")))
	            		p.sendMessage(U.c(m.c.getString(con+"alreadyGuarded")));
	                return;
	            }
                if (isOnCooldown(u)) {
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", getCooldownTime(u)+""));
                    return;
                }
                addCooldown(p);
                guarded.add(p.getName());
                VAPI.useAbility(p, con);
                U.remove1Item(p);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onQuit(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player)e.getEntity();
            if (p.getHealth() - e.getFinalDamage() <= m.c.getDouble(con+"needHearts") && isGuarded(p)) {
                e.setDamage(0);
                p.setHealth(p.getMaxHealth());
                guarded.remove(p.getName());
            }
        }
    }
     
    public boolean isGuarded(Player p) {
    	return guarded.contains(p.getName());
    }
    
}

package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class FullInvis extends Ability implements Listener {
    public HashMap<UUID, ItemStack[]> invisedPlayers;
    private Main m;

    public FullInvis(Main m) {
        super("Abilities.FullInvis.", "FULLINVIS", new VItemStack(Material.getMaterial(m.c.getString("Abilities.FullInvis.type").toUpperCase()), m.c.getString("Abilities.FullInvis.displayname"), m.c.getStringList("Abilities.FullInvis.lore"), (short)m.c.getInt("Abilities.FullInvis.data")));
    	this.m = m;
        invisedPlayers = new HashMap<UUID, ItemStack[]>();
        Bukkit.getPluginManager().registerEvents(this, m);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (Utils.isShit(item))
            return;
        Player p = e.getPlayer();
        if (isSimilar(item)) {
            UUID pd = p.getUniqueId();
            if (e.getAction().toString().contains("RIGHT")) {
	            PlayerInventory inv = p.getInventory();
	            if (invisedPlayers.containsKey(p.getUniqueId())) {
	            	if(!StringUtils.isNullOrEmpty(m.c.getString(con+"alreadyInvisible")))
	            		p.sendMessage(U.c(m.c.getString(con+"alreadyInvisible")));
	                return;
	            }
	            if (isOnCooldown(pd)) {
	                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(pd))));
	                return;
	            }
	            invisedPlayers.put(p.getUniqueId(), inv.getArmorContents());
	            inv.setHelmet(null);
	            inv.setChestplate(null);
	            inv.setLeggings(null);
	            inv.setBoots(null);
	            U.remove1Item(p);
	            p.updateInventory();
	            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, m.c.getInt(con + "useTime") * 20, 1));
	            Bukkit.getScheduler().runTaskLater(m, () -> {
	            	if(p != null)
	            		returnItems(p);
	            }, (m.c.getInt(con + "useTime") * 20));
	            VAPI.useAbility(p, con);
	            addCooldown(p);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() != DamageCause.FALL && invisedPlayers.containsKey(e.getEntity().getUniqueId())) {
            returnItems((Player)e.getEntity());
            e.setDamage(2.0);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	returnItems(e.getPlayer());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
    	final UUID q = e.getEntity().getUniqueId();
        if (invisedPlayers.containsKey(q))
        	invisedPlayers.remove(q);
    }
    
    public void returnItems(final Player p) {
    	final UUID q = p.getUniqueId();
        if (invisedPlayers.containsKey(q)) {
        	final PlayerInventory inv = p.getInventory();
            if (inv.getHelmet() != null) {
                inv.addItem(inv.getHelmet());
                inv.setHelmet(invisedPlayers.get(q)[3]);
            } else {
                inv.setHelmet(invisedPlayers.get(q)[3]);
            }
            if (inv.getChestplate() != null) {
                inv.addItem(inv.getChestplate());
                inv.setChestplate(invisedPlayers.get(q)[2]);
            } else {
                inv.setChestplate(invisedPlayers.get(q)[2]);
            }
            if (inv.getLeggings() != null) {
                inv.addItem(inv.getLeggings());
                inv.setLeggings(invisedPlayers.get(q)[1]);
            } else {
                inv.setLeggings(invisedPlayers.get(q)[1]);
            }
            if (inv.getBoots() != null) {
                inv.addItem(inv.getBoots());
                inv.setBoots(invisedPlayers.get(q)[0]);
            } else {
                inv.setBoots(invisedPlayers.get(q)[0]);
            }
            invisedPlayers.remove(q);
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
    
    public boolean isInvised(UUID uuid) {
    	return invisedPlayers.containsKey(uuid);
    }

}

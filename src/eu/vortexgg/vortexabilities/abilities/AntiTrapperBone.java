package eu.vortexgg.vortexabilities.abilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class AntiTrapperBone extends Ability implements Listener {
    public HashMap<String, Integer> hits;
    public HashMap<String, Long> bonedPlayers;
    private Main m;
    
    public AntiTrapperBone(Main m) {
        super("Abilities.AntiTrapperBone.", "ANTITRAPPERBONE", new VItemStack(Material.getMaterial(m.c.getString("Abilities.AntiTrapperBone.type").toUpperCase()), m.c.getString("Abilities.AntiTrapperBone.displayname"), m.c.getStringList("Abilities.AntiTrapperBone.lore"), (short)m.c.getInt("Abilities.AntiTrapperBone.data")));
    	this.m = m;
    	bonedPlayers = new HashMap<String, Long>();
    	hits = new HashMap<String, Integer>();
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            Player d = (Player)entity;
            Player p = (Player)damager;
            ItemStack item = p.getItemInHand();
            if (Utils.isShit(item))
                return;
            if (isSimilar(item)) {
                UUID u = p.getUniqueId();
                if (!isOnCooldown(u)) {
                    if (bonedPlayers.containsKey(d.getName())) {
                        p.sendMessage(U.c(m.c.getString(con + "alreadyBoned").replace("%enemy_displayname%", d.getDisplayName())));
                        return;
                    }
                    if (hits.containsKey(p.getName())) {
                        hits.put(p.getName(), hits.get(p.getName()) + 1);
                    } else {
                        hits.put(p.getName(), 1);
                        Bukkit.getScheduler().runTaskLater(m, () -> {
                            if (p != null && hits.containsKey(p.getName())) {
                                hits.remove(p.getName());
                            }
                            return;
                        }, (m.c.getInt(con + "resetHits") * 20));
                    }
                    if (hits.get(p.getName()) >= m.c.getInt(con + "needHits")) {
                        VAPI.useAbility(p, d, con);
                        addCooldown(p);
                        bonedPlayers.put(d.getName(), System.currentTimeMillis());
                        Bukkit.getScheduler().runTaskLater(m, () -> {
                            if (bonedPlayers.containsKey(d.getName()))
                                bonedPlayers.remove(d.getName());
                        }, (m.c.getInt(con + "useTime") * 20));
                        hits.remove(p.getName());
                        U.remove1Item(p);
                    }
                } else {
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (isBoned(p)) {
            e.setCancelled(true);
            p.sendMessage(U.c(m.c.getString(con + "cantInteract").replace("%time%", String.valueOf(getBoneTime(p)))));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (isBoned(p)) {
            e.setCancelled(true);
            p.sendMessage(U.c(m.c.getString(con + "cantInteract").replace("%time%", String.valueOf(getBoneTime(p)))));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block c = e.getClickedBlock();
            Player p = e.getPlayer();
            if (U.isOpenable(c.getType(), con) && !p.isSneaking() && isBoned(p)) {
                e.setCancelled(true);
                p.sendMessage(U.c(m.c.getString(con + "cantInteract").replace("%time%", String.valueOf(getBoneTime(p)))));
            }
        }
    }

    public boolean isBoned(Player p) {
        String name = p.getName();
        return bonedPlayers.get(name) != null && bonedPlayers.get(name) + m.c.getInt(con + "useTime") * 1000 > System.currentTimeMillis();
    }
    
    public int getBoneTime(Player p) {
        int f = (int)(System.currentTimeMillis() - bonedPlayers.get(p.getName()));
        return m.c.getInt(con + "useTime") - f / 1000;
    }
}

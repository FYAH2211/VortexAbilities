package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class PotionRefill extends Ability implements Listener {
    private Main m;
    
    public PotionRefill(Main m) {
        super("Abilities.PotionRefill.", "POTREFILL", new VItemStack(Material.getMaterial(m.c.getString("Abilities.PotionRefill.type").toUpperCase()), m.c.getString("Abilities.PotionRefill.displayname"), m.c.getStringList("Abilities.PotionRefill.lore"), (short)m.c.getInt("Abilities.PotionRefill.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
            if (e.getAction().toString().contains("RIGHT")) {
            	UUID u = p.getUniqueId();
                if (isOnCooldown(u)) {
                    e.setCancelled(true);
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                    return;
                }
                PlayerInventory inv = p.getInventory();
                ItemStack potion = new ItemStack(Material.POTION, 1, (short)16421);
                for (int i = 0; i < m.c.getInt(con+"potionsToRefill"); ++i) {
                    if (inv.firstEmpty() != -1) {
                        inv.addItem(potion);
                    } else {
                        p.getWorld().dropItemNaturally(p.getLocation(), potion);
                    }
                }
                p.updateInventory();
                VAPI.useAbility(p, con);
                U.remove1Item(p);
                addCooldown(p);
            }
        }
    }

}

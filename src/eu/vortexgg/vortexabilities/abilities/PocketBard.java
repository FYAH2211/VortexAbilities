package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class PocketBard extends Ability implements Listener {
    private Main m;
    
    public PocketBard(Main m) {
        super("Abilities.PocketBard.", "POCKETBARD", new VItemStack(Material.getMaterial(m.c.getString("Abilities.PocketBard.type").toUpperCase()), m.c.getString("Abilities.PocketBard.displayname"), m.c.getStringList("Abilities.PocketBard.lore"), (short)m.c.getInt("Abilities.PocketBard.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if(inv == null)
        	return;
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player)e.getWhoClicked();
            if (inv.getTitle().equals(U.c(m.c.getString("AbilitiesInventory.name"))))
                e.setCancelled(true);
            if (inv.getTitle().equals(U.c(m.c.getString(con + "Menu.title")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    switch (e.getCurrentItem().getType()) {
                    case FEATHER: {
                    	p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, m.c.getInt(con + "Menu.Items.JumpBoost.DETAILS.duration") * 20, m.c.getInt(con + "Menu.Items.JumpBoost.DETAILS.level") - 1));
                    	p.sendMessage(U.c(m.c.getString(con + "Menu.Items.JumpBoost.DETAILS.message")));
                    	p.closeInventory();
                    	U.remove1Item(p);
                    	addCooldown(p);
                    	break;
                    }
                    case BLAZE_POWDER: {
                    	p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, m.c.getInt(con + "Menu.Items.Strength.DETAILS.duration") * 20, m.c.getInt(con + "Menu.Items.Strength.DETAILS.level") - 1));
                    	p.sendMessage(U.c(m.c.getString(con + "Menu.Items.Strength.DETAILS.message")));
                    	p.closeInventory();
                    	U.remove1Item(p);
                    	addCooldown(p);
                    	break;
                    }
                    case IRON_INGOT: {
                    	p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, m.c.getInt(con + "Menu.Items.Resistance.DETAILS.duration") * 20, m.c.getInt(con + "Menu.Items.Resistance.DETAILS.level") - 1));
                    	p.sendMessage(U.c(m.c.getString(con + "Menu.Items.Resistance.DETAILS.message")));
                    	p.closeInventory();
                    	U.remove1Item(p);
                    	addCooldown(p);
                    	break;
                    }
                    case GHAST_TEAR: {
                    	p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, m.c.getInt(con + "Menu.Items.Regeneration.DETAILS.duration") * 20, m.c.getInt(con + "Menu.Items.Regeneration.DETAILS.level") - 1));
                    	p.sendMessage(U.c(m.c.getString(con + "Menu.Items.Regeneration.DETAILS.message")));
                    	p.closeInventory();
                    	U.remove1Item(p);
                    	addCooldown(p);
                    	break;
                    }
					default:
						break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (Utils.isShit(item)) 
            return;
        Action action = e.getAction();
        if (isSimilar(item)) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            	UUID u = p.getUniqueId();
                if (isOnCooldown(u)) {
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                    return;
                }
                applyPocketBard(p);
            }
        }
    }
    
    public void applyPocketBard(Player player) {
        Inventory pocketbard = Bukkit.createInventory(null, 27, U.c(m.c.getString(con + "Menu.title")));
        ItemStack strength = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta strengthmeta = strength.getItemMeta();
        strengthmeta.setDisplayName(U.c(m.c.getString(con + "Menu.Items.Strength.name")));
        strengthmeta.setLore(U.cL(m.c.getStringList(con + "Menu.Items.Strength.lore")));
        Utils.addGlow1710(strength);
        strength.setItemMeta(strengthmeta);
        ItemStack res = new ItemStack(Material.IRON_INGOT);
        ItemMeta resmeta = res.getItemMeta();
        resmeta.setDisplayName(U.c(m.c.getString(con + "Menu.Items.Resistance.name")));
        resmeta.setLore(U.cL(m.c.getStringList(con + "Menu.Items.Resistance.lore")));
        Utils.addGlow1710(res);
        res.setItemMeta(resmeta);
        ItemStack reg = new ItemStack(Material.GHAST_TEAR);
        ItemMeta regmeta = reg.getItemMeta();
        regmeta.setDisplayName(U.c(m.c.getString(con + "Menu.Items.Regeneration.name")));
        regmeta.setLore(U.cL(m.c.getStringList(con + "Menu.Items.Regeneration.lore")));
        Utils.addGlow1710(reg);
        reg.setItemMeta(regmeta);
        ItemStack jump = new ItemStack(Material.FEATHER);
        ItemMeta jumpmeta = jump.getItemMeta();
        jumpmeta.setDisplayName(U.c(m.c.getString(con + "Menu.Items.JumpBoost.name")));
        jumpmeta.setLore(U.cL(m.c.getStringList(con + "Menu.Items.JumpBoost.lore")));
        Utils.addGlow1710(jump);
        jump.setItemMeta(jumpmeta);
        pocketbard.setItem(10, strength);
        pocketbard.setItem(12, jump);
        pocketbard.setItem(14, res);
        pocketbard.setItem(16, reg);
        player.openInventory(pocketbard);
    }

}

package eu.vortexgg.vortexabilities.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class WebGun extends Ability implements Listener {

    private Main m;
    
    public WebGun(Main m) {
        super("Abilities.WebGun.", "WEBGUN", new VItemStack(Material.getMaterial(m.c.getString("Abilities.WebGun.type").toUpperCase()), m.c.getString("Abilities.WebGun.displayname"), m.c.getStringList("Abilities.WebGun.lore"), (short)m.c.getInt("Abilities.WebGun.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Egg) || !(e.getEntity().getShooter() instanceof Player))
            return;
        Egg egg = (Egg)e.getEntity();
        Player p = (Player)egg.getShooter();
        ItemStack item = p.getItemInHand();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item) ) {
        	UUID u = p.getUniqueId();
            if (isOnCooldown(u)) {
                e.setCancelled(true);
                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%",String.valueOf(getCooldownTime(u))));
                return;
            }
            egg.setMetadata("web", new FixedMetadataValue(m, true));
            VAPI.useAbility(p, con);
            addCooldown(p);
        }
    }
    
    @EventHandler
    public void onHit(ProjectileHitEvent e) {
    	if(e.getEntity() instanceof Egg) {
    		final Egg s = (Egg)e.getEntity();
    		if (s.hasMetadata("web")) {
    			final Location location = s.getLocation();
    			final Block hitBlock = location.getBlock();
    			if(hitBlock.getType() == Material.AIR) {
    				final List<Block> blockToReturn = new ArrayList<Block>();
    				final Block west = hitBlock.getRelative(BlockFace.WEST);
    				final Block east = hitBlock.getRelative(BlockFace.EAST);
    				final Block north = hitBlock.getRelative(BlockFace.NORTH);
    				final Block south = hitBlock.getRelative(BlockFace.SOUTH);
    				final Block se = hitBlock.getRelative(BlockFace.SOUTH_EAST);
    				final Block ne = hitBlock.getRelative(BlockFace.NORTH_EAST);
    				final Block nw = hitBlock.getRelative(BlockFace.NORTH_WEST);
    				final Block sw = hitBlock.getRelative(BlockFace.SOUTH_WEST);
    				blockToReturn.add(hitBlock);
    				hitBlock.setType(Material.WEB);
    				if(east.getType() == Material.AIR) {
    					blockToReturn.add(east);
    					east.setType(Material.WEB);
    				} 
    				if(south.getType() == Material.AIR) {
    					blockToReturn.add(south);
    					south.setType(Material.WEB);
    				} 
    				if(north.getType() == Material.AIR) {
    					blockToReturn.add(north);
    					north.setType(Material.WEB);
    				} 
    				if(west.getType() == Material.AIR) {
    					blockToReturn.add(west);
    					west.setType(Material.WEB);
    				} 
    				if(sw.getType() == Material.AIR) {
    					blockToReturn.add(sw);
    					sw.setType(Material.WEB);
    				} 
    				if(se.getType() == Material.AIR) {
    					blockToReturn.add(se);
    					se.setType(Material.WEB);
    				} 
    				if(ne.getType() == Material.AIR) {
    					blockToReturn.add(ne);
    					ne.setType(Material.WEB);
    				} 
    				if(nw.getType() == Material.AIR) {
    					blockToReturn.add(nw);
    					nw.setType(Material.WEB);
    				}
    				Bukkit.getScheduler().runTaskLater(m, () -> {
    					for(Block block : blockToReturn)
    						if(block.getType() == Material.WEB)
    							block.setType(Material.AIR);
    				}, m.c.getInt(con+"secondsToRemoveWeb")*20);
    			}
    		}
    	}
    }

}

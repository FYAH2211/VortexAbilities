package eu.vortexgg.vortexabilities.abilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class PrePearl extends Ability implements Listener {
    private Main m;
    
    public PrePearl(Main m) {
        super("Abilities.PrePearl.", "PREPEARL", new VItemStack(Material.getMaterial(m.c.getString("Abilities.PrePearl.type").toUpperCase()), m.c.getString("Abilities.PrePearl.displayname"), m.c.getStringList("Abilities.PrePearl.lore"), (short)m.c.getInt("Abilities.PrePearl.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EnderPearl) || !(e.getEntity().getShooter() instanceof Player))
            return;
        EnderPearl snowBall = (EnderPearl)e.getEntity();
        Player p = (Player)snowBall.getShooter();
        ItemStack item = p.getItemInHand();
        if (Utils.isShit(item))
            return;
        if (isSimilar(item)) {
        	UUID u = p.getUniqueId();
            if (isOnCooldown(u)) {
                e.setCancelled(true);
                p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%",String.valueOf(getCooldownTime(u))));
                return;
            }
            Location location = p.getLocation();
            Bukkit.getScheduler().runTaskLater(m, () -> {
                if (p.isOnline()) {
                	if(!isSafe(location.getBlock()) && m.c.getBoolean(con+"checkSafeLocation")) {
                        String msgToShooter = U.r(m.c.getString(con + "locationNotSafe"),null,null);
                        if (msgToShooter != null)
                            p.sendMessage(msgToShooter);
                        return;
                	} else {
                		p.teleport(location);
                	}
                }
            }, (m.c.getInt(con + "timeToTeleportBack") * 20));
            VAPI.useAbility(p, con);
            addCooldown(p);
        }
    }

    private boolean isSafe(Block block) {
    	Material mat = block.getType();
    	Material mat2 = block.getRelative(BlockFace.UP).getType();
    	return (mat == Material.AIR || mat.toString().contains("GLASS")) && (mat2 == Material.AIR || mat2.toString().contains("GLASS"));
    }

}

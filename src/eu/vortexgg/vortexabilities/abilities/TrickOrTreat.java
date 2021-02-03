package eu.vortexgg.vortexabilities.abilities;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.api.U;
import eu.vortexgg.vortexabilities.api.Utils;
import eu.vortexgg.vortexabilities.api.VAPI;
import eu.vortexgg.vortexabilities.structure.Ability;
import eu.vortexgg.vortexabilities.structure.VItemStack;

public class TrickOrTreat extends Ability implements Listener {

    private Main m;
    
    public TrickOrTreat(Main m) {
        super("Abilities.TrickOrTreat.", "TRICKORTREAT", new VItemStack(Material.getMaterial(m.c.getString("Abilities.TrickOrTreat.type").toUpperCase()), m.c.getString("Abilities.TrickOrTreat.displayname"), m.c.getStringList("Abilities.TrickOrTreat.lore"), (short)m.c.getInt("Abilities.TrickOrTreat.data")));
    	this.m = m;
        Bukkit.getServer().getPluginManager().registerEvents(this, m);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();;
        if (Utils.isShit(item))
            return;
        Player p = e.getPlayer();
        if (isSimilar(item)) {
            if (e.getAction().toString().contains("RIGHT")) {
            	UUID u = p.getUniqueId();
                if (isOnCooldown(u)) {
                    e.setCancelled(true);
                    p.sendMessage(U.c(m.c.getString(con + "cooldownMessage")).replace("%time%", String.valueOf(getCooldownTime(u))));
                    return;
                }
                VAPI.useAbility(p, con);

                if((new Random().nextInt(100) >= m.c.getInt(con+"trickChance"))) {
                    String msgTrick = U.r(Main.inst.c.getString(con + "shooterTrickMessage"), p, null);
                    if (msgTrick != null)
                        p.sendMessage(msgTrick);
                    VAPI.giveAbilityEffects(p, con+"trickEffects");
                } else {
                    String msgThreat = U.r(Main.inst.c.getString(con + "shooterThreatMessage"), p, null);
                    if (msgThreat != null)
                        p.sendMessage(msgThreat);
                    VAPI.giveAbilityEffects(p, con+"threatEffects");
                }
                addCooldown(p);
                U.remove1Item(p);
            }	
        }
    }

}

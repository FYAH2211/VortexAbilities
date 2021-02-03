package eu.vortexgg.vortexabilities.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;

public class VAPI {
	
    public static void useAbility(Player shooter, Player hitplayer, String con) {
        if (shooter == null || hitplayer == null)
            return;
        String msgToShooter = U.r(Main.inst.c.getString(con + "shooterMessage"),shooter,hitplayer);
        String msgToHit = U.r(Main.inst.c.getString(con + "enemyMessage"),shooter,hitplayer);
        if (msgToShooter != null)
            shooter.sendMessage(msgToShooter);
        if (msgToHit != null)
            hitplayer.sendMessage(msgToHit);
        giveAbilityEffects(shooter, con + "shooterEffects");
        giveAbilityEffects(hitplayer, con + "enemyEffects");
        playAbilitySounds(shooter, con + "shooterSounds");
        playAbilitySounds(hitplayer, con + "enemySounds");
        sendAbilityTitle(shooter, con + "shooterTitle");
        sendAbilityTitle(hitplayer, con + "enemyTitle");
        executeAbilityCommands(shooter.getName(), hitplayer.getName(), con + "commands");
    }
    
    public static void useAbility(Player shooter, String con) {
        if (shooter == null)
            return;
        String msgToShooter = U.r(Main.inst.c.getString(con + "shooterMessage"), shooter);
        if (msgToShooter != null)
            shooter.sendMessage(msgToShooter);
        giveAbilityEffects(shooter, con + "shooterEffects");
        playAbilitySounds(shooter, con + "shooterSounds");
        sendAbilityTitle(shooter, con + "shooterTitle");
        executeAbilityCommands(shooter.getName(), "", con + "commands");
    }
    
    public static void giveAbilityEffects(Player hitted, String config) {
        if (StringUtils.isNullOrEmpty(config))
            return;
        List<String> list = Main.inst.c.getStringList(config);
        if (list.isEmpty() || list == null)
            return;
        for (String po : list) {
            String[] inf = po.split(":");
            if (inf.length == 3 && PotionEffectType.getByName(inf[0].toUpperCase()) != null) {
                PotionEffectType pot = PotionEffectType.getByName(inf[0].toUpperCase());
                int dur = 0;
                int amp = 0;
                try {
                    dur = Math.min(Integer.parseInt(inf[1]) * 20, 99999999);
                } catch (NumberFormatException e1) {
                    dur = Integer.MAX_VALUE;
                }
                try {
                    amp = Math.min(Integer.parseInt(inf[2]) - 1, 255);
                } catch (NumberFormatException e1) {
                    amp = 255;
                }
                if (hitted.hasPotionEffect(pot))
                    continue;
                PotionEffect potionEffect = new PotionEffect(pot, dur, amp);
                hitted.addPotionEffect(potionEffect);
            }
        }
    }
    
    public static void playAbilitySounds(Player p, String config) {
        if (StringUtils.isNullOrEmpty(config))
            return;
        List<String> list = Main.inst.c.getStringList(config);
        if (list.isEmpty() || list == null)
            return;
        for (String po : list) {
            Sound sound = null;
            try {
                sound = Sound.valueOf(po.toUpperCase());
            } catch (IllegalArgumentException a) {
                Main.inst.getLogger().info("The sound '" + sound + "' is invalid!");
                return;
            }
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    @SuppressWarnings("deprecation")
	public static void sendAbilityTitle(Player p, String config) {
        if (!Utils.is1710()) {
            if (!StringUtils.isNullOrEmpty(config)) {
                String title = Main.inst.c.getString(config);
                if (StringUtils.isNullOrEmpty(title)) {
                    return;
                }
                if (title.contains("|")) {
                    String[] s = title.split("\\|");
                    p.sendTitle(s[0], s[1]);
                }
                else {
                    p.sendTitle(title, "");
                }
            }
        }
    }
    
    public static void executeAbilityCommands(String p, String f, String config) {
        if (StringUtils.isNullOrEmpty(config) || (StringUtils.isNullOrEmpty(f) && StringUtils.isNullOrEmpty(p)))
            return;
        List<String> list = Main.inst.c.getStringList(config);
        if (list.isEmpty() || list == null)
            return;
        for (String po : list) {
            Player shooter = Bukkit.getPlayer(p);
            Player enemy = Bukkit.getPlayer(f);
            if (enemy != null)
                po = po.replace("%enemy_name%", f);
            if (shooter != null)
                po = po.replace("%player_name%", p);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), po);
        }
    }
}

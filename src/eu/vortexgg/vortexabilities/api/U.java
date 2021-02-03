package eu.vortexgg.vortexabilities.api;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.mysql.jdbc.StringUtils;

import eu.vortexgg.vortexabilities.Main;

public class U {
    public static final TimeZone SERVER_TIME_ZONE = Calendar.getInstance().getTimeZone();
    public static final ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
    public static final FastDateFormat DAY_MTH_HR_MIN = FastDateFormat.getInstance("dd/MM HH:mm", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM/yy hh:mma", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat HR_MIN = FastDateFormat.getInstance("hh:mm", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat MIN_SECS = FastDateFormat.getInstance("mm:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat KOTH_FORMAT = FastDateFormat.getInstance("m:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final FastDateFormat PALACE_FORMAT = FastDateFormat.getInstance("m:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.#");
        }
    };
    public static final ThreadLocal<DecimalFormat> SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0"));

    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));
    
    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    
    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < TimeUnit.MINUTES.toMillis(1)) {
            return (trail ? REMAINING_SECONDS_TRAILING : REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= TimeUnit.HOURS.toMillis(1)) ? "HH:" : "") + "mm:ss");
    }
    
    public static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    
    public static List<String> cL(List<String> s) {
        ArrayList<String> toReturn = new ArrayList<String>();
        for (String st : s)
            toReturn.add(c(st));
        return toReturn;
    }
    
    public static boolean isOpenable(Material q, String con) {
    	if(Main.inst.c.getBoolean(con+"disableOpeningGatesOrDoors"))
    		return q.toString().contains("TRAP_DOOR") || q.toString().contains("FENCE_GATE") || q.toString().contains("DOOR");
        return q.toString().contains("MINECART") || q == Material.FURNACE || q.toString().contains("ANVIL") || q == Material.ENCHANTMENT_TABLE || q.toString().contains("REDSTONE") || q == Material.HOPPER ||q == Material.LEVER ||  q.toString().contains("BUTTON") || q.toString().contains("PISTON") || q.toString().contains("CHEST");
    }
    
    public static String r(String string, Player shooter) {
    	return r(string, shooter, null);
    }
    public static final Integer tryParseInt(final String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static LivingEntity rayTraceEntity(Player player, int range) {
    	BlockIterator iterator = new BlockIterator(player.getWorld(), player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), 0, range);
    	Chunk chunk = null;
    	Entity[] entities = null;
    	while (iterator.hasNext()) {
    		Location l = iterator.next().getLocation();
    		if (chunk != l.getChunk()) {
    			chunk = l.getChunk();
    			entities = chunk.getEntities();
    		} 
    		if (entities != null && entities.length > 0) {
   				for (Entity entity : entities) {
   					if (entity != player && entity instanceof LivingEntity && entity.getType() != EntityType.SQUID && l.getWorld().getName() == entity.getLocation().getWorld().getName() && l.distance(entity.getLocation()) < 1.5)
   						return (LivingEntity)entity; 
   				} 
    		} 
    	} 
    	return null;
    }
    
    public static String r(String string, Player shooter, Player enemy) {
    	if(StringUtils.isNullOrEmpty(string))
    		return null;
    	if(shooter != null) {
    		string = string.replaceAll("%shooter_displayname%", shooter.getDisplayName());
    		string = string.replaceAll("%shooter_name%", shooter.getName());
    	}
    	if(enemy != null) {
    		string = string.replaceAll("%enemy_displayname%", enemy.getDisplayName());
    		string = string.replaceAll("%enemy_name%", enemy.getName());
    	}
    	return c(string);
    }
    
    public static void remove1Item(Player i) {
        ItemStack it = i.getItemInHand();
        int a = it.getAmount();
        if (a > 1)
            it.setAmount(a - 1);
        else
            i.setItemInHand(null);
    }
    
    public static boolean isntFullInv(Player p) {
        return p.getInventory().firstEmpty() != -1;
    }
}

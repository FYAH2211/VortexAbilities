package eu.vortexgg.vortexabilities.api;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

public class Utils {    
	
    public static boolean isShit(ItemStack i) {
        return i == null || i.getType() == Material.AIR || !i.getItemMeta().hasDisplayName();
    }
    public static boolean is1710() {
        return Bukkit.getVersion().contains("1.7");
    }
    
    public static ItemStack addGlow1710(ItemStack item) {
    	net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
    	NBTTagCompound tag = null;
    	try {
    		if (!nmsStack.hasTag() || nmsStack.getTag() == null) {
    			tag = new net.minecraft.server.v1_7_R4.NBTTagCompound();
    		} else {
    			tag = nmsStack.getTag();
    		}
    	} catch (NullPointerException ex) { 
    		return item; 
    	}
    	NBTTagList ench = new net.minecraft.server.v1_7_R4.NBTTagList();
    	tag.set("ench", (net.minecraft.server.v1_7_R4.NBTBase)ench);
    	nmsStack.setTag(tag);
    	return org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asCraftMirror(nmsStack);
    }
}

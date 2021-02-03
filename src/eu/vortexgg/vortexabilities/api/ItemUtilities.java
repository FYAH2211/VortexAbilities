package eu.vortexgg.vortexabilities.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtilities {
    public static String serialize(ItemStack item, Integer slot) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getType().toString());
        if (item.getDurability() != 0) {
            builder.append(":" + item.getDurability());
        }
        builder.append(" " + item.getAmount());
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            builder.append(" " + enchant.getName() + ":" + item.getEnchantments().get(enchant));
        }
        String name = getName(item);
        if (name != null) {
            builder.append(" name:" + name);
        }
        String lore = getLore(item);
        if (lore != null) {
            builder.append(" lore:" + lore);
        }
        Color color = getArmorColor(item);
        if (color != null) {
            builder.append(" rgb:" + color.getRed() + "|" + color.getGreen() + "|" + color.getBlue());
        }
        String owner = getOwner(item);
        if (owner != null) {
            builder.append(" owner:" + owner);
        }
        if (slot >= 0) {
            builder.append(" slot:" + slot + 1);
        }
        else {
            slot = 0;
            Bukkit.getLogger().warning("Slot can`t be smaller than 0");
        }
        return builder.toString();
    }
    
    public static HashMap<ItemStack, Integer> deserialize(String serializedItem) {
        String[] strings = serializedItem.split(" ");
        HashMap<ItemStack, Integer> toReturn = new HashMap<ItemStack, Integer>();
        int slot = 0;
        Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        ItemStack item = new ItemStack(Material.AIR);
        int i = 0;
        while (i < strings.length) {
            String str = strings[i];
            String[] args = str.split(":");
            if (Material.matchMaterial(args[0]) != null && item.getType() == Material.AIR) {
                item.setType(Material.matchMaterial(args[0]));
                if (args.length == 2) {
                    item.setDurability(Short.parseShort(args[1]));
                    break;
                }
                break;
            } else {
                ++i;
            }
        }
        if (item.getType() == Material.AIR) {
            Bukkit.getLogger().info("Could not find a valid material for the item in \"" + serializedItem + "\"");
            return null;
        }
        for (i = 0; i < strings.length; ++i) {
            String str = strings[i];
            String[] args = str.split(":", 2);
            if (isNumber(args[0])) {
                item.setAmount(Integer.parseInt(args[0]));
            }
            if (args.length != 1) {
                if (args[0].equalsIgnoreCase("name")) {
                    setName(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                }
                else if (args[0].equalsIgnoreCase("lore")) {
                    setLore(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                }
                else if (args[0].equalsIgnoreCase("rgb")) {
                    setArmorColor(item, args[1]);
                }
                else if (args[0].equalsIgnoreCase("owner")) {
                    setOwner(item, args[1]);
                }
                else if (Enchantment.getByName(args[0].toUpperCase()) != null) {
                    enchants.put(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
                }
                if (args[0].equalsIgnoreCase("slot")) {
                    slot = Integer.parseInt(args[1]) - 1;
                }
                else {
                    slot = 0;
                }
            }
        }
        item.addUnsafeEnchantments(enchants);
        if (item.getType() != Material.AIR) {
            toReturn.put(item, slot);
        }
        return toReturn.isEmpty() ? null : toReturn;
    }
    
    private static String getOwner(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            return null;
        }
        return ((SkullMeta)item.getItemMeta()).getOwner();
    }
    
    private static void setOwner(ItemStack item, String owner) {
        try {
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwner(owner);
            item.setItemMeta((ItemMeta)meta);
        }
        catch (Exception ex) {}
    }
    
    private static String getName(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        if (!item.getItemMeta().hasDisplayName()) {
            return null;
        }
        return item.getItemMeta().getDisplayName().replace(" ", "_").replace('§', '&');
    }
    
    private static void setName(ItemStack item, String name) {
        name = name.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }
    
    private static String getLore(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        if (!item.getItemMeta().hasLore()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        List<String> lore = item.getItemMeta().getLore();
        for (int ind = 0; ind < lore.size(); ++ind) {
            builder.append(String.valueOf((ind > 0) ? "|" : "") + lore.get(ind).replace(" ", "_").replace('§', '&'));
        }
        return builder.toString();
    }
    
    private static void setLore(ItemStack item, String lore) {
        lore = lore.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore.split("\\|")));
        item.setItemMeta(meta);
    }
    
    private static Color getArmorColor(ItemStack item) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
            return null;
        }
        return ((LeatherArmorMeta)item.getItemMeta()).getColor();
    }
    
    private static void setArmorColor(ItemStack item, String str) {
        try {
            String[] colors = str.split("\\|");
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setColor(Color.fromRGB(red, green, blue));
            item.setItemMeta(meta);
        } catch (Exception ex) {}
    }
    
    private static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        }
        catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}

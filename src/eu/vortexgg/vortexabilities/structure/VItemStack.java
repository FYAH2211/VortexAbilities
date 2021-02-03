package eu.vortexgg.vortexabilities.structure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionType;

public class VItemStack extends ItemStack {
	public VItemStack(Material m, String name, String desc, Object... datas) {
		this(m, 1, name, desc, datas);
	}
  
	public VItemStack(PotionType pt, int level, boolean extended_duration, boolean splash, int amount, String name) {
		this(Material.POTION, amount, name, null, new Object[0]);
	}
	
	public VItemStack(Material m, int amount, String name, String desc, Object... datas) {
		super(m, amount);
		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		setItemMeta(im);
		if (desc != null && !desc.isEmpty()) {
			String[] splitted = desc.split("\\|");
			List<String> lore = new ArrayList<>();
			for (String s : splitted) {
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			im.setLore(lore);
			setItemMeta(im);
		} 
		if (datas == null || datas.length == 0)
			return; 
		for (int i = 0; i < datas.length; i++) {
			Object data = datas[i];
			if (data instanceof Color) {
				try {
					LeatherArmorMeta lam = (LeatherArmorMeta)im;
					lam.setColor((Color)data);
					setItemMeta((ItemMeta)lam);
				} catch (Exception exception) {}
			} else if (data instanceof Enchantment && datas[i + 1] instanceof Integer) {
				addUnsafeEnchantment((Enchantment)data, ((Integer)datas[i + 1]).intValue());
				i++;
			} else if (data instanceof Integer) {
				setAmount(((Integer)data).intValue());
			} else if (data instanceof Short) {
				setDurability(((Short)data).shortValue());
			} 
		} 
 	}
	
	public VItemStack(Material m, String name, List<String> desc, Object... datas) {
		super(m);
		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		setItemMeta(im);
		if (desc != null && !desc.isEmpty()) {
			List<String> lore = new ArrayList<>();
			for (String s : desc)
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			im.setLore(lore);
			setItemMeta(im);
		} 
		if (datas == null || datas.length == 0)
			return; 
		for (int i = 0; i < datas.length; i++) {
			Object data = datas[i];
			if (data instanceof Color) {
				try {
					LeatherArmorMeta lam = (LeatherArmorMeta)im;
					lam.setColor((Color)data);
					setItemMeta((ItemMeta)lam);
				} catch (Exception exception) {}
			} else if (data instanceof Enchantment && datas[i + 1] instanceof Integer) {
				addUnsafeEnchantment((Enchantment)data, ((Integer)datas[i + 1]).intValue());
				i++;
			} else if (data instanceof Integer) {
				setAmount(((Integer)data).intValue());
			} else if (data instanceof Short) {
				setDurability(((Short)data).shortValue());
			} 
		} 
 	}
		
	public VItemStack(Material m, int amount, String name) {
		this(m, amount, name, "", (Object[])null);
	}
	
	public VItemStack(Material m, String name) {
		this(m, name, "");
	}
  
	public VItemStack(Material m) {
		this(m, (String)null);
	}
	
	public VItemStack(Material m, String name, String desc) {
		this(m, name, desc, (Object[])null);
	}
  
	public VItemStack(Material m, String name, Object... objects) {
		this(m, name, "", objects);
	}

}

package eu.vortexgg.vortexabilities;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

public enum Particles {
	  ANGRY_VILLAGER("angryVillager"),
	  BUBBLE("bubble"),
	  CLOUD("cloud"),
	  CRIT("crit"),
	  DEPTH_SUSPEND("depthsuspend"),
	  DRIP_LAVA("dripLava"),
	  DRIP_WATER("dripWater"),
	  ENCHANTMENT_TABLE("enchantmenttable"),
	  EXPLODE("explode"),
	  FIREWORKS_SPARK("fireworksSpark"),
	  FLAME("flame"),
	  FOOTSTEP("footstep"),
	  HAPPY_VILLAGER("happyVillager"),
	  HEART("heart"),
	  HUGE_EXPLOSION("hugeexplosion"),
	  INSTANT_SPELL("instantSpell"),
	  LARGE_EXPLODE("largeexplode"),
	  LARGE_SMOKE("largesmoke"),
	  LAVA("lava"),
	  MAGIC_CRIT("magicCrit"),
	  MOB_SPELL_AMBIENT("mobSpellAmbient"),
	  MOB_SPELL("mobSpell"),
	  NOTE("note"),
	  PORTAL("portal"),
	  RED_DUST("reddust"),
	  SLIME("slime"),
	  SMOKE("smoke"),
	  SNOW_SHOVEL("snowshovel"),
	  SNOWBALL_POOF("snowballpoof"),
	  SPELL("spell"),
	  SPLASH("splash"),
	  SUSPENDED("suspended"),
	  WITCH_MAGIC("witchMagic"),
	  TOWNAURA("townaura");
  
	private final String id;
	
	Particles(String id) {
		this.id = id;
	}
		  
	public void play(World world, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player... players) {
		play(world, this.id, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
	}
		  
	public void play(Location loc, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player... players) {
		play(loc.getWorld(), this.id, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
	}
	
	public static void playIconCrack(World world, int id, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player... players) {
		play(world, "iconcrack_" + id, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
	}
	
	public static void playTileCrack(World world, int id, int meta, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player... players) {
		play(world, "tilecrack_" + id + "_" + meta, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles, players);
	}
	
	public static void play(World world, String particle, float x, float y, float z, float xOffset, float yOffset, float zOffset, float effectSpeed, int amountOfParticles, Player... players) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, x, y, z, xOffset, yOffset, zOffset, effectSpeed, amountOfParticles);
		if (players.length == 0) {
			int radius = 400;
			List<EntityPlayer> list = ((CraftWorld)world).getHandle().players;
			for (EntityPlayer player : list) {
				double distanceSquared = NumberConversions.square(player.locX - x) + NumberConversions.square(player.locY - y) + NumberConversions.square(player.locZ - z);
				if (distanceSquared < radius)
					player.playerConnection.sendPacket(packet); 
			} 
		} else {
			for (Player player : players)
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		} 
	}

}

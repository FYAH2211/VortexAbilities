package eu.vortexgg.vortexabilities.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.vortexgg.vortexabilities.Main;
import eu.vortexgg.vortexabilities.abilities.AimKick;
import eu.vortexgg.vortexabilities.abilities.AntiTrapperBone;
import eu.vortexgg.vortexabilities.abilities.ConfusionStick;
import eu.vortexgg.vortexabilities.abilities.DamageRepulser;
import eu.vortexgg.vortexabilities.abilities.DisarmerAxe;
import eu.vortexgg.vortexabilities.abilities.ExplosiveBow;
import eu.vortexgg.vortexabilities.abilities.FullInvis;
import eu.vortexgg.vortexabilities.abilities.GuardianAngel;
import eu.vortexgg.vortexabilities.abilities.PocketBard;
import eu.vortexgg.vortexabilities.abilities.PotionRefill;
import eu.vortexgg.vortexabilities.abilities.PrePearl;
import eu.vortexgg.vortexabilities.abilities.RocketBooster;
import eu.vortexgg.vortexabilities.abilities.RottenEgg;
import eu.vortexgg.vortexabilities.abilities.SwitcherSnowball;
import eu.vortexgg.vortexabilities.abilities.Telekinesis;
import eu.vortexgg.vortexabilities.abilities.TrickOrTreat;
import eu.vortexgg.vortexabilities.abilities.WeaponBreaker;
import eu.vortexgg.vortexabilities.abilities.WebGun;

public class AbilityManager {
	
	private Set<Ability> abilities;
    public RottenEgg rottenEgg;
    public SwitcherSnowball switcherSnowball;
    public AntiTrapperBone atBone;
    public DisarmerAxe disarmerAxe;
    public RocketBooster rocketBooster;
    public PocketBard pocketBard;
    public GuardianAngel guardianAngel;
    public PotionRefill potionRefill;
    public FullInvis fullInvis;
    public ConfusionStick confusionStick;
    public TrickOrTreat trickOrThreat;
    public PrePearl prePearl;
    public AimKick aimKick;
    public WebGun webGun;
    public Telekinesis telekinesis;
    public DamageRepulser damageRepulser;
    public WeaponBreaker weaponBreaker;
    public ExplosiveBow explosiveBow;
    
	public AbilityManager(Main plugin) {
		abilities = new HashSet<Ability>();
		registerAbilities(plugin);
	}
	
    private void registerAbilities(Main p) {
    	registerAbility(rottenEgg = new RottenEgg(p));
        registerAbility(switcherSnowball = new SwitcherSnowball(p));
        registerAbility(atBone = new AntiTrapperBone(p));
        registerAbility(disarmerAxe = new DisarmerAxe(p));
        registerAbility(rocketBooster = new RocketBooster(p));
        registerAbility(pocketBard = new PocketBard(p));
        registerAbility(webGun = new WebGun(p));
        registerAbility(prePearl = new PrePearl(p));
        registerAbility(guardianAngel = new GuardianAngel(p));
        registerAbility(potionRefill = new PotionRefill(p));
        registerAbility(fullInvis = new FullInvis(p));
        registerAbility(confusionStick = new ConfusionStick(p));
        registerAbility(trickOrThreat = new TrickOrTreat(p));
        registerAbility(explosiveBow = new ExplosiveBow(p));
        registerAbility(aimKick = new AimKick(p));
        registerAbility(telekinesis = new Telekinesis(p));
        registerAbility(damageRepulser = new DamageRepulser(p));
        registerAbility(weaponBreaker = new WeaponBreaker(p));
    }
    
	public Ability getAbilityByName(String name) {
		for(Ability a : abilities) 
			if(a.name.equalsIgnoreCase(name))
				return a;
		return null;
	}
	
	public void registerAbility(Ability ability) {
		abilities.add(ability);
	}
	
	public Collection<Ability> getAbilities() {
		return abilities;
	}
	
    public void unregisterAbility(Ability ability) {
    	if(abilities.contains(ability))
    		abilities.remove(ability);
    }
}

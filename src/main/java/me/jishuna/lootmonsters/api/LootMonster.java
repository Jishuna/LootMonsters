package me.jishuna.lootmonsters.api;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import me.jishuna.commonlib.random.WeightedRandom;

public class LootMonster {

	private final String name;
	private final String displayName;
	private final double weight;
	private final EntityType entityType;

	private final int minDrops;
	private final int maxDrops;

	private final double health;

	private final ItemStack[] armor = new ItemStack[4];
	private ItemStack mainHand;
	private ItemStack offHand;

	private final Set<PotionEffect> effects = new HashSet<>();
	private final Set<String> replaces;
	private final WeightedRandom<ItemStack> loot = new WeightedRandom<>();

	public LootMonster(ConfigurationSection section) {
		this.name = section.getString("name");
		this.displayName = Utils.colorString(section.getString("display-name", this.name));

		this.weight = section.getDouble("weight", 100);

		this.entityType = EntityType.valueOf(section.getString("entity-type", "zombie").toUpperCase());

		this.replaces = section.getStringList("replaces").stream().map(String::toUpperCase).collect(Collectors.toSet());

		this.minDrops = section.getInt("min-drops", 1);
		this.maxDrops = section.getInt("max-drops", 3);

		this.health = section.getDouble("health", 20);

		ConfigurationSection gearSection = section.getConfigurationSection("equipment");

		if (gearSection != null) {
			armor[3] = Utils.parseItem(this, gearSection.getConfigurationSection("helmet"));
			armor[2] = Utils.parseItem(this, gearSection.getConfigurationSection("chestplate"));
			armor[1] = Utils.parseItem(this, gearSection.getConfigurationSection("leggings"));
			armor[0] = Utils.parseItem(this, gearSection.getConfigurationSection("boots"));

			this.mainHand = Utils.parseItem(this, gearSection.getConfigurationSection("main-hand"));
			this.offHand = Utils.parseItem(this, gearSection.getConfigurationSection("off-hand"));
		}

		ConfigurationSection lootSection = section.getConfigurationSection("loot");

		if (lootSection != null) {
			for (String key : lootSection.getKeys(false)) {
				ConfigurationSection lootEntry = lootSection.getConfigurationSection(key);

				this.loot.add(lootEntry.getDouble("weight", 100), Utils.parseItem(this, lootEntry));
			}
		}

		for (String effect : section.getStringList("potion-effects")) {
			PotionEffect potionEffect = Utils.parsePotionEffect(this, effect);
			
			if (potionEffect != null)
				this.effects.add(potionEffect);
		}
	}

	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	public boolean canReplace(EntityType type) {
		return this.replaces.contains(type.toString());
	}

	public void spawn(Location location) {
		World world = location.getWorld();

		Entity entity = world.spawn(location, this.entityType.getEntityClass());
		entity.setCustomName(this.displayName);

		entity.getPersistentDataContainer().set(PluginKeys.MONSTER_TYPE.getKey(), PersistentDataType.STRING, this.name);
		entity.setPersistent(false);

		if (!(entity instanceof LivingEntity livingEntity))
			return;

		this.effects.forEach(livingEntity::addPotionEffect);

		livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.health);
		livingEntity.setHealth(this.health);

		EntityEquipment equipment = livingEntity.getEquipment();
		equipment.setArmorContents(this.armor);
		equipment.setItemInMainHand(this.mainHand);
		equipment.setItemInOffHand(this.offHand);

		equipment.setHelmetDropChance(0.0f);
		equipment.setChestplateDropChance(0.0f);
		equipment.setLeggingsDropChance(0.0f);
		equipment.setBootsDropChance(0.0f);
		equipment.setItemInMainHandDropChance(0.0f);
		equipment.setItemInOffHandDropChance(0.0f);
	}

	public void handleDeath(EntityDeathEvent event) {
		event.getDrops().clear();

		int count = ThreadLocalRandom.current().nextInt(this.minDrops, this.maxDrops + 1);
		for (int i = 0; i < count; i++) {
			event.getDrops().add(this.loot.poll());
		}
	}
}

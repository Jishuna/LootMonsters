package me.jishuna.lootmonsters.api;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.EntityType;

import me.jishuna.commonlib.random.WeightedRandom;

public class MonsterRegistry {

	private final EnumMap<EntityType, WeightedRandom<LootMonster>> typeCache = new EnumMap<>(EntityType.class);
	private final Map<String, LootMonster> registryMap = new HashMap<>();

	public void registerMonster(LootMonster monster) {
		this.registryMap.put(monster.getName(), monster);
	}
	
	public void reload() {
		this.typeCache.clear();
		this.registryMap.clear();
	}

	public LootMonster getRandomMonster(EntityType original) {
		WeightedRandom<LootMonster> random = this.typeCache.get(original);

		if (random == null) {
			random = new WeightedRandom<>();

			for (LootMonster monster : this.registryMap.values()) {
				if (monster.canReplace(original)) {
					double weight = monster.getWeight();

					if (weight > 0)
						random.add(weight, monster);
				}
			}
			this.typeCache.put(original, random);
		}
		return random.isEmpty() ? null : random.poll();
	}

	public Optional<LootMonster> getMonster(String key) {
		return Optional.ofNullable(this.registryMap.get(key));
	}
	
	public int getTotalRegistered() {
		return this.registryMap.size();
	}
}
